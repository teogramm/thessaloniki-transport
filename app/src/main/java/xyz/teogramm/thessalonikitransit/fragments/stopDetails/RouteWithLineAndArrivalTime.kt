package xyz.teogramm.thessalonikitransit.fragments.stopDetails

import xyz.teogramm.thessalonikitransit.database.transit.entities.Line
import xyz.teogramm.thessalonikitransit.database.transit.entities.Route

data class RouteWithLineAndArrivalTime(
    val route: Route,
    val line: Line,
    var arrivalTimes: List<Int>?
)