package xyz.teogramm.thessalonikitransport.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import xyz.teogramm.thessalonikitransport.R
import xyz.teogramm.thessalonikitransport.database.transit.alerts.CompleteAlert
import xyz.teogramm.thessalonikitransport.repositories.AlertsRepository
import xyz.teogramm.thessalonikitransport.repositories.LiveDataRepository
import java.time.LocalTime
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Service which monitors the arrival times of busses at stops and sends notifications when a bus is arriving sooner
 * than the configured threshold.
 */
@AndroidEntryPoint
class AlertService: LifecycleService() {

    companion object{
        // TODO: Maybe move these to AlertServiceActions
        /**
         * Name of the extra which contains the stopID for which the corresponding action is performed. Should be
         * used in Intents sent to this service.
         */
        const val STOPID_EXTRA_NAME = "stopId"
        private const val PERSISTENT_NOTIFICATION_ID = 100
    }

    /**
     * Interface containing callbacks for actions performed by the [AlertService]
     */
    interface AlertServiceListener {
        /**
         * Called when the alert service is stopped and is no longer monitoring any stops.
         */
        fun onServiceStopped()
    }

    private class AlertWithArrivalTime(
        val serviceAlert: ServiceAlert,
        var arrivalTime: Int? = null
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as AlertWithArrivalTime

            if (serviceAlert != other.serviceAlert) return false

            return true
        }

        override fun hashCode(): Int {
            return serviceAlert.hashCode()
        }
    }

    inner class AlertServiceBinder: Binder(){
        fun getService(): AlertService = this@AlertService
    }

    /**
     * A custom coroutine scope, used to manage coroutines launched by this service (e.g. periodic update of
     * arrival times)
     */
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    // TODO: Make this configurable
    /**
     * Configures how often the arrival times are refreshed.
     */
    private val REFRESH_TIME_SECONDS = 30L
    // TODO: Show this in persistent notification
    private var lastUpdateTime: LocalTime? = null
    /**
     * Whether an error was encountered when updating the arrival times.
     */
    private var error = false
    /**
     * Monitored stop IDs. Used when fetching the arrival times for each stop.
     */
    private val stops = mutableSetOf<Int>()
    /**
     * Contains [AlertWithArrivalTime] objects for all Stop-Route pairs which are monitored by the service.
     */
    private val alerts = mutableSetOf<AlertWithArrivalTime>()
    /**
     *  Service responsible for running periodic updates
     */
    private var updateService = Executors.newSingleThreadScheduledExecutor()
    @Inject lateinit var liveDataRepository: LiveDataRepository
    @Inject lateinit var alertsRepository: AlertsRepository
    /**
     * Maps stopIds to jobs which collect from flows exposed by the database.
     * The jobs must be cancelled when a stop is no longer monitored to avoid leaks.
     */
    private val stopIdsToJobs = mutableMapOf<Int,Job>()
    private var listener: AlertServiceListener? = null

    private val binder = AlertServiceBinder()

    /**
     * Start executing periodic updates.
     */
    private fun startPeriodicUpdates(){
        // Update every REFRESH_TIME_SECONDS seconds
        val handler = Handler(Looper.getMainLooper())
        // Create a new executor service and schedule the task
        updateService = Executors.newSingleThreadScheduledExecutor()
        updateService.scheduleAtFixedRate({
            handler.run {
                update()
            }
        }, 0, REFRESH_TIME_SECONDS, TimeUnit.SECONDS)
    }

    /**
     * Fetch updated data, update the persistent notification and notify the user of arriving buses.
     */
    private fun update(){
        serviceScope.launch {
            if (stops.isNotEmpty()) {
                refreshTimes()
                withContext(Dispatchers.Main) {
                    updatePersistentNotification()
                    notifyUser()
                }
            }
        }
    }
    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val action = intent?.action
        if(action!= null){
            when(AlertServiceActions.valueOf(action)){
                AlertServiceActions.ADD_ALERT ->{
                    // If this is the first alert we add create the foreground notification and start updating the
                    // data periodically
                    if(stops.isEmpty()){
                        // TODO: Update this to show something meaningful
                        val builder = NotificationCompat.Builder(this, Notifications.PERSISTENT_CHANNEL_NAME)
                            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                            .setContentTitle("AAAA").setContentText("AAAA")
                            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        startForeground(PERSISTENT_NOTIFICATION_ID, builder.build())
                        updatePersistentNotification()
                        startPeriodicUpdates()
                    }
                    val stopId = intent.getIntExtra(STOPID_EXTRA_NAME, Int.MIN_VALUE)
                    if(stopId != Int.MIN_VALUE){
                        startMonitoringStop(stopId)
                    }
                }
                AlertServiceActions.REMOVE_ALERT ->{
                    val stopId = intent.getIntExtra(STOPID_EXTRA_NAME, Int.MIN_VALUE)
                    if(stopId != Int.MIN_VALUE){
                        stopMonitoringStop(stopId)
                    }
                }
                AlertServiceActions.UPDATE_ARRIVALS ->{
                    update()
                }
                AlertServiceActions.STOP_SERVICE ->{
                    Log.d("AlertsService", "Received close intent")
                    clear()
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            }
        }

        return START_NOT_STICKY
    }

    /**
     * Start fetching data for the stop with the given stop ID. The alerts configured for this stop are fetched
     * from the database.
     */
    private fun startMonitoringStop(stopId: Int){
        // In case there is already a job for this stop cancel it to avoid leaks.
        stopIdsToJobs[stopId]?.cancel()
        stopIdsToJobs[stopId] = serviceScope.launch {
            alertsRepository.getStopAlerts(stopId).collectLatest {newAlerts ->
                Log.d("AlertService", "Updating alerts for ${newAlerts.stop.stop.nameEL}")
                // When the alerts for a stop are modified, remove the old alerts and add the new ones
                removeStopAlerts(newAlerts.stop.stop.stopId)
                addStopAlerts(newAlerts)
            }
        }
        update()
    }

    private fun stopMonitoringStop(stopId: Int){
        if(stopId != Int.MIN_VALUE) {
            // Stop monitoring the database for changes
            stopIdsToJobs[stopId]?.cancel()
            removeStopAlerts(stopId)
        }
        if(stops.isEmpty()){
            // Stop service if we remove the last alerts
            clear()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    /**
     * Adds new alerts for a stop based on the information contained in the given [CompleteAlert] object.
     * @param newAlerts Contains the alerts which will be added to the service. If [CompleteAlert.routes] is empty
     * then no alerts are added to the database.
     */
    private fun addStopAlerts(newAlerts: CompleteAlert){
        if(newAlerts.routes.isNotEmpty()){
            stops.add(newAlerts.stop.stop.stopId)
            alerts.addAll(newAlerts.routes.map { routeWithLine ->
                val sa = ServiceAlert(newAlerts.stop.stop.stopId, routeWithLine.route.routeId,
                    newAlerts.stop.stop.nameEL, routeWithLine.line.number, newAlerts.stop.notificationThreshold.first())
                Log.d("AlertService", "Add ${newAlerts.stop.stop.nameEL} - ${routeWithLine.line.number}")
                AlertWithArrivalTime(sa)
            })
        }
    }

    /**
     * Removes all alerts for the given stopId. Does not stop monitoring for any changes for this stop.
     */
    private fun removeStopAlerts(stopId: Int){
        stops.remove(stopId)
        alerts.removeIf { it.serviceAlert.stopId == stopId }
    }

    /**
     * Update the persistent notification
     */
    private fun updatePersistentNotification(){
        val fastestBus = alerts.minByOrNull { it.arrivalTime?: Int.MAX_VALUE }
        val view = RemoteViews(packageName, R.layout.notification_small)
        // Check if the fastest bus is null (no arrivals for any stop-route pair)
        val text = if(fastestBus?.arrivalTime != null){
            getString(R.string.busArrivingDetails, fastestBus.serviceAlert.lineNumber, fastestBus.arrivalTime, fastestBus.serviceAlert.stopName)
        }else{
            getString(R.string.persistentNotificationNoData)
        }
        view.setTextViewText(R.id.fastestArrivalLabel, text)

        // Configure the Stop service button
        val stopIntent = Intent(this, AlertService::class.java)
        stopIntent.action = AlertServiceActions.STOP_SERVICE.toString()
        val pendingIntent = PendingIntent
            .getService(applicationContext, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)
        view.setOnClickPendingIntent(R.id.persistentNotificationCloseButton, pendingIntent)

        val notification = NotificationCompat.Builder(this, Notifications.PERSISTENT_CHANNEL_NAME)
            .setOnlyAlertOnce(true)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setCustomContentView(view)
            .setCustomBigContentView(view)
            .build()

        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(PERSISTENT_NOTIFICATION_ID, notification)
    }

    /**
     * Get new arrival times for all configured alerts.
     */
    private suspend fun refreshTimes(){
        stops.forEach { stopId ->
            // For each stop fetch the arriving buses
            val routeIdToArrivalTimes = try{
                liveDataRepository.getStopArrivals(stopId)
            }catch (e: Exception){
                error = true
                return
            }
            routeIdToArrivalTimes.let {
                // Get all the alerts for the current stop
                alerts.filter { it.serviceAlert.stopId == stopId }.forEach { alert ->
                    // Set the arrival time to the received value if it exists, otherwise we do not have an
                    // arrival time
                    alert.arrivalTime = routeIdToArrivalTimes[alert.serviceAlert.routeId]?.minOrNull()
                }
            }
        }
    }

    /**
     * Stops monitoring all stops, cancels all update jobs and notifies the listener that the service is stopping.
     * Should be called before stopping the service as it performs cleanup tasks.
     */
    private fun clear(){
        // Stop any in progress activities
        serviceScope.coroutineContext.cancelChildren()
        // Stop the service responsible for running periodic updates
        updateService.shutdownNow()
        // Stop collecting new alerts from the database
        stopIdsToJobs.values.forEach { it.cancel() }
        stops.clear()
        alerts.clear()
        // Notify the listener if it's attached
        listener?.onServiceStopped()
    }

    override fun onDestroy() {
        clear()
        super.onDestroy()
    }

    /**
     * Sends notifications for all alerts that have busses arriving within the configured threshold.
     */
    private fun notifyUser(){
        alerts.forEach { alert ->
            alert.arrivalTime?.let { arrivalTime ->
                if(arrivalTime <= alert.serviceAlert.notificationTime) {
                    val builder = NotificationCompat.Builder(this, Notifications.ARRIVALS_CHANNEL_NAME)
                    builder.setContentTitle(getString(R.string.notificationArrivalTitle))
                        .setContentText(getString(R.string.busArrivingDetails,
                            alert.serviceAlert.lineNumber, alert.arrivalTime, alert.serviceAlert.stopName))
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp).setOnlyAlertOnce(true)
                    val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    // Use route ID as notification ID to avoid spam. Use hashCode to avoid colliding
                    // with PERSISTENT_NOTIFICATION_ID
                    mNotificationManager.notify(alert.serviceAlert.routeId.hashCode(), builder.build())
                }
            }
        }
    }

    /**
     * Return the IDs of the stops we are currently monitoring
     */
    fun getActiveAlertStopIds(): Set<Int>{
        return stops.toSet()
    }

    /**
     * Set the listener for callbacks by this service
     * @see [AlertService.AlertServiceListener]
     */
    fun setListener(listener: AlertServiceListener?){
        this.listener = listener
    }
}