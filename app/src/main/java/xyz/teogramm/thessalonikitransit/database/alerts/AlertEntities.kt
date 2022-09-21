package xyz.teogramm.thessalonikitransit.database.alerts

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Contains a stopId - routeId pair for which the user wants to be notified.
 */
@Entity
data class UserAlert (
    @PrimaryKey val stopId: Int,
    val routeId: Int
)

@Entity(tableName = "stoptime")
data class StopNotificationTime (
    @PrimaryKey val stopId: Int,
    val notificationTimeMinutes: Int
    )