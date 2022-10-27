package xyz.teogramm.thessalonikitransit.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import xyz.teogramm.thessalonikitransit.database.transit.TransitDatabase
import xyz.teogramm.thessalonikitransit.database.transit.alerts.Alert
import xyz.teogramm.thessalonikitransit.database.transit.alerts.StopNotificationTime
import javax.inject.Inject

class AlertsRepository @Inject constructor(
    transitDatabase: TransitDatabase
) {
    private val alertsDao = transitDatabase.alertDao()

    /**
     * Add alert for the given routes passing through the stop.
     * @param stopId ID of the stop to monitor
     * @param routeIds IDs of the routes to monitor
     */
    suspend fun addAlert(stopId: Int, routeIds: Iterable<Int>, notificationMinutes: Int) = withContext(Dispatchers.IO + NonCancellable){
        if(notificationMinutes < 1){
            throw IllegalArgumentException("Notification time must be positive.")
        }
        // TODO: Make this more efficient by calculating needed removals/additions instead of removing everything
        // First remove any existing alerts for this stop.
        alertsDao.deleteStopAlerts(stopId)
        // We don't need to delete the notification time as it is updated on conflict
        alertsDao.addStopThreshold(StopNotificationTime(stopId, notificationMinutes))
        // Map each routeId to a stopId-routeId pair to insert it in the database
        val newAlerts = routeIds.map { routeId -> Alert(stopId, routeId) }
        alertsDao.addAlerts(newAlerts)
    }

    fun getAllAlerts() = alertsDao.getAllAlerts()
    fun getStopAlerts(stopId: Int) = alertsDao.getStopAlerts(stopId)

}