package xyz.teogramm.thessalonikitransport.database.transit.alerts

import androidx.room.*
import xyz.teogramm.thessalonikitransport.database.transit.entities.Route
import xyz.teogramm.thessalonikitransport.database.transit.entities.RouteWithLine
import xyz.teogramm.thessalonikitransport.database.transit.entities.Stop

/**
 * Contains a stopId - routeId - notificationTIme triplet for which the user wants to be notified.
 * Only a single notification time can be specified for a stopId - routeId pair.
 * A StopNotificationTime entry must already exist for this database before inserting an alert
 */
@Entity(
    foreignKeys = [
        ForeignKey(entity = Stop::class, parentColumns = ["stopId"], childColumns = ["stopId"]),
        ForeignKey(entity = Route::class, parentColumns = ["routeId"], childColumns = ["routeId"]),
        ForeignKey(entity = StopNotificationTime::class, parentColumns = ["stopId"], childColumns = ["stopId"] )],
    // Create index for routeId as it is used in the CompleteAlert relationship
    indices = [Index("routeId")],
    primaryKeys = ["stopId","routeId"]
)
data class Alert (
    val stopId: Int,
    val routeId: Int,
)

/**
 * Contains an alert with complete information about the stop of the alert, the route and the notification threshold
 */
// This is bad to use since we need to do tricks to fetch all the relations
// TODO: Don't use this outside the database, replace with a better wrapper class at the repository
data class CompleteAlert(
    @Embedded val stop: StopWithNotificationThreshold,
    @Relation(
        parentColumn = "stopId",
        entityColumn = "routeId",
        entity = Route::class,
        associateBy = Junction(Alert::class)
    )
    val routes: List<RouteWithLine>
)

/**
 * Contains a stop and its associated notification threshold. [notificationThreshold] is a list that is guaranteed
 * to contain only 1 element: the notification threshold for the given stop.
 */
data class StopWithNotificationThreshold(
    @Embedded val stop: Stop,
    @Relation(
        parentColumn = "stopId",
        entityColumn = "stopId",
        entity = StopNotificationTime::class,
        // Keep only the notification threshold
        projection = ["notificationThreshold"]
    )
    val notificationThreshold: List<Int>
)

/**
 * Contains the time limit, after which the user will be notified that a line is arriving at the given stop.
 * The notification threshold is defined per stop.
 * @param notificationThreshold Notification threshold in minutes
 */
@Entity(foreignKeys = [
    ForeignKey(entity = Stop::class, parentColumns = ["stopId"], childColumns = ["stopId"]),
])
data class StopNotificationTime(
    @PrimaryKey val stopId: Int,
    val notificationThreshold: Int
)