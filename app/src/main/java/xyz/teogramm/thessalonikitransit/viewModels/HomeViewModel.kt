package xyz.teogramm.thessalonikitransit.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import xyz.teogramm.thessalonikitransit.fragments.home.HomeUiState
import xyz.teogramm.thessalonikitransit.fragments.home.StopAlerts
import xyz.teogramm.thessalonikitransit.fragments.stopDetails.RouteWithLineAndArrivalTime
import xyz.teogramm.thessalonikitransit.repositories.AlertsRepository
import xyz.teogramm.thessalonikitransit.repositories.StaticDataRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val alertsRepository: AlertsRepository,
                                        private val staticDataRepository: StaticDataRepository): ViewModel(){

    @OptIn(ExperimentalCoroutinesApi::class)
    val allAlerts = alertsRepository.getAllAlerts().transformLatest {
        // Convert CompleteAlert objects to StopAlerts object for displaying in the UI
        val stopAlerts = it.map { completeAlert -> StopAlerts(
            completeAlert.stop.stop,
            completeAlert.routes.map { routeWithLine ->
                // Add dummy arrival times
                // TODO: Fetch the arrival times from the foreground service
                RouteWithLineAndArrivalTime(routeWithLine.route, routeWithLine.line, null)
            })
        }
        emit(HomeUiState(stopAlerts))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = HomeUiState(emptyList())
    )
}