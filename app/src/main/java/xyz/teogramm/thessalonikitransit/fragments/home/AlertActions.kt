package xyz.teogramm.thessalonikitransit.fragments.home

import xyz.teogramm.thessalonikitransit.database.transit.entities.Stop

interface AlertActions {
    fun onStopEditButtonPressed(stop: Stop)
    fun onStopAlertsEnabled(stopAlerts: StopAlerts)
    fun onStopAlertsDisabled(stopAlerts: StopAlerts)
}