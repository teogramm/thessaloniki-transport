package xyz.teogramm.thessalonikitransit.fragments.home

import xyz.teogramm.thessalonikitransit.database.transit.alerts.CompleteAlert
import xyz.teogramm.thessalonikitransit.database.transit.entities.Stop
import xyz.teogramm.thessalonikitransit.fragments.stopDetails.RouteWithLineAndArrivalTime

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