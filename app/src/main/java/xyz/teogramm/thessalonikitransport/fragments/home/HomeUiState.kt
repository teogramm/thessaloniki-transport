package xyz.teogramm.thessalonikitransport.fragments.home

import xyz.teogramm.thessalonikitransport.database.transit.entities.Stop
import xyz.teogramm.thessalonikitransport.fragments.stopDetails.RouteWithLineAndArrivalTime

data class HomeUiState (
    val alerts: List<StopAlerts>
)

/**
 * Contains inf
 */
data class StopAlerts(
    val stop: Stop,
    val lines: List<RouteWithLineAndArrivalTime>,
    val notificationTime: Int
)