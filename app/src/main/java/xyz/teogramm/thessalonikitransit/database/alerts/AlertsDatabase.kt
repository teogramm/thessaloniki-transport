package xyz.teogramm.thessalonikitransit.database.alerts

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 1,
    entities = [UserAlert::class, StopNotificationTime::class]
)
abstract class AlertsDatabase: RoomDatabase() {
    abstract fun alertsDao(): AlertsDao
}