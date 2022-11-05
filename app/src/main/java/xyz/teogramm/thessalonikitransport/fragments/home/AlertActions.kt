package xyz.teogramm.thessalonikitransport.fragments.home

import xyz.teogramm.thessalonikitransport.database.transit.entities.Stop

interface AlertActions {
    fun onStopEditButtonPressed(stop: Stop)
    fun onStopAlertsEnabled(stopAlerts: StopAlerts)
    fun onStopAlertsDisabled(stopAlerts: StopAlerts)
}