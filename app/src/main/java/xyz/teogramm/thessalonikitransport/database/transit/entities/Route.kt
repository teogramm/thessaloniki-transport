package xyz.teogramm.thessalonikitransport.database.transit.entities

import androidx.room.*

@Entity(
    foreignKeys = [
        ForeignKey(entity = Line::class, parentColumns = arrayOf("lineId"), childColumns = arrayOf("lineId"))
    ],
    indices = [
        // Used when searching for all routes of a line
        Index(value = ["lineId"])
    ]
)
/**
 * A route is essentially a series of stops. Each route belongs to a specific line.
 *
 * [type] field means:
 * 1 - outbound route
 * 2 - return route
 * 3 - circular route
 */
class Route(
    @PrimaryKey var routeId: Int,
    val nameEL: String,
    val nameEN: String,
    val type: Int,
    val lineId: Int
)

@Entity(
    primaryKeys = ["routeId", "stopId", "stopIndex"],
    foreignKeys = [
        ForeignKey(entity = Route::class, parentColumns = ["routeId"], childColumns = ["routeId"]),
        ForeignKey(entity = Stop::class, parentColumns = ["stopId"], childColumns = ["stopId"])
    ],
    indices = [
        // Used when searching for all stops of a route
        Index(value = ["routeId"])
    ]
)
data class RouteStop(
    // Stop index is included in the key because a stop can appear
    // in a route multiple times.
    val routeId: Int,
    val stopId: Int,
    val stopIndex: Int
)

/**
 * Contains a route along with the line it belongs to.
 */
data class RouteWithLine(
    @Embedded val route: Route,
    @Relation(
        entity = Line::class,
        parentColumn = "lineId",
        entityColumn = "lineId"
    )
    val line:Line
)

/**
 * Database view that returns all RouteStop entries for last stops of routes.
 */
@DatabaseView("""SELECT * FROM RouteStop GROUP BY routeId HAVING MAX(stopIndex);""")
data class LastRouteStops(
    @Embedded val routeStop: RouteStop
)

/**
 * Database view that returns all information about routes and their last stops.
 */
@DatabaseView("""SELECT * FROM LastRouteStops INNER JOIN ROUTE ON Route.routeId = LastRouteStops.routeId""")
data class RouteWithLastStop(
    @Embedded val route: Route,
    @Relation(
        parentColumn = "routeId",
        entityColumn = "stopId",
        associateBy = Junction(value = LastRouteStops::class, parentColumn = "routeId", entityColumn = "stopId")
    )
    val lastStop: Stop
)
