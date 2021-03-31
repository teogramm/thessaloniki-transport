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

@DatabaseView("""SELECT Line.*, stopId FROM RouteStop INNER JOIN Route ON Route.routeId = RouteStop.routeId 
                       INNER JOIN Line ON Line.lineId = Route.lineId;""")
data class StopsWithLines(
    @Embedded val line: Line,
    val stopId: Int
)