package xyz.teogramm.thessalonikitransit.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import xyz.teogramm.thessalonikitransit.database.alerts.AlertsDatabase
import xyz.teogramm.thessalonikitransit.database.alerts.StopNotificationTime
import xyz.teogramm.thessalonikitransit.database.alerts.UserAlert
import javax.inject.Inject

class AlertsRepository @Inject constructor(
    private val alertsDatabase: AlertsDatabase
) {
    private val alertsDao = alertsDatabase.alertsDao()

    /**
     * Add alert for the given routes passing through the stop.
     * @param stopId ID of the stop to monitor
     * @param routeIds IDs of the routes to monitor
     */
    suspend fun addAlert(stopId: Int, routeIds: Iterable<Int>, notificationMinutes: Int) = withContext(Dispatchers.IO){
        if(notificationMinutes < 1){
            throw IllegalArgumentException("Notification")
        }
        // First remove any existing alerts for this stop.
        alertsDao.deleteUserAlert(stopId)
        // We don't need to delete the notification time as it is updated on conflict
        alertsDao.addStopNotificationTime(StopNotificationTime(stopId,notificationMinutes))
        // Map each routeId to a stopId-routeId pair to insert it in the database
        val userAlerts = routeIds.map { id -> UserAlert(stopId,id) }
        alertsDao.addUserAlerts(userAlerts)
    }

    /**
     * Remove all alerts for the given stop.
     */
    suspend fun removeAlert(stopId: Int) = withContext(Dispatchers.IO){
        alertsDao.deleteUserAlert(stopId)
        alertsDao.deleteNotificationTime(stopId)
    }
}