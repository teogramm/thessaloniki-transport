package xyz.teogramm.thessalonikitransit.fragments.alerts

import xyz.teogramm.thessalonikitransit.database.transit.entities.Line

data class CreateAlertDialogUiState (
    val notificationTimeMinutes: Int?,
    val lines: List<Line>,
    /**
     * Lines with alerts enabled
     */
    val enabled: Set<Line>
    )