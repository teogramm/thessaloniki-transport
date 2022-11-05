package xyz.teogramm.thessalonikitransport.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import xyz.teogramm.thessalonikitransport.fragments.home.HomeUiState
import xyz.teogramm.thessalonikitransport.fragments.home.StopAlerts
import xyz.teogramm.thessalonikitransport.fragments.stopDetails.RouteWithLineAndArrivalTime
import xyz.teogramm.thessalonikitransport.repositories.AlertsRepository
import xyz.teogramm.thessalonikitransport.repositories.StaticDataRepository
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
            },
            completeAlert.stop.notificationThreshold.first())
        }
        emit(HomeUiState(stopAlerts))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = HomeUiState(emptyList())
    )
}