package xyz.teogramm.thessalonikitransit.database.transit.entities

import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Stop(
    @PrimaryKey var stopId: Int,
    var publicId: String,
    var nameEL: String,
    var nameEN: String,
    var heading: Int,
    var longitude: Double,
    var latitude: Double
)

@DatabaseView("""SELECT Line.*, Route.nameEL AS routeNameEL, Route.nameEN AS routeNameEN, 
                       Route.routeId, Route.type AS routeType, stopId FROM RouteStop 
                       INNER JOIN Route ON Route.routeId = RouteStop.routeId 
                       INNER JOIN Line ON Line.lineId = Route.lineId;""")
/**
 * Contains information about a Route amd its corresponding route that pass through a stop with ID [stopId]
 */
data class StopsWithRoutesAndLines(
    @Embedded val line: Line,
    val routeNameEL: String,
    val routeNameEN: String,
    val routeId: Int,
    val routeType: Int,
    val stopId: Int
)