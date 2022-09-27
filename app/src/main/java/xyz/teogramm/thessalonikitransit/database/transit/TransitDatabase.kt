package xyz.teogramm.thessalonikitransit.database.transit

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import xyz.teogramm.thessalonikitransit.database.transit.alerts.Alert
import xyz.teogramm.thessalonikitransit.database.transit.alerts.AlertDao
import xyz.teogramm.thessalonikitransit.database.transit.alerts.StopNotificationTime
import xyz.teogramm.thessalonikitransit.database.transit.entities.*

@Database(
    version = 1,
    entities = [
        Calendar::class, Schedule::class, ScheduleTime::class, MasterLine::class,
        Line::class, Route::class, RouteStop::class, Stop::class, Alert::class, StopNotificationTime::class
    ],
    views = [RouteWithLastStop::class, LastRouteStops::class, StopsWithRoutesAndLines::class]
)
@TypeConverters(TransitConverters::class)
abstract class TransitDatabase : RoomDatabase() {
    abstract fun transitDao(): TransitDao
    abstract fun alertDao(): AlertDao
}
