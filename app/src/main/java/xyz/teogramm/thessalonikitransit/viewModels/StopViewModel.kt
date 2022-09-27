package xyz.teogramm.thessalonikitransit.viewModels

import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import xyz.teogramm.thessalonikitransit.database.transit.entities.Stop
import xyz.teogramm.thessalonikitransit.fragments.stopDetails.RouteWithLineAndArrivalTime
import xyz.teogramm.thessalonikitransit.repositories.LiveDataRepository
import xyz.teogramm.thessalonikitransit.repositories.StaticDataRepository
import javax.inject.Inject

/**
 * ViewModel containing information about a specific stop
 */
@HiltViewModel
class StopViewModel @Inject constructor(private val staticRepository: StaticDataRepository,
                                        private val liveDataRepository: LiveDataRepository): ViewModel() {

    // Use a dummy stop as the initial value
    private val _stop = MutableStateFlow(Stop(0, "", "", "", 0, 0.0, 0.0))

    /**
     * Contains the currently selected stop
     */
    val stop = _stop.asStateFlow()


    /**
     * This initially fetches lines and routes from the database. Emits [RouteWithLineAndArrivalTime] objects, without
     * populating the arrival time field.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _routesWithLines = _stop.transformLatest { stop ->
        viewModelScope.launch {
            emit(emptyList())
            val routesWithLines = withContext(Dispatchers.IO) {
                staticRepository.getRoutesWithLinesForStop(stop)
            }
            emit(routesWithLines.map { RouteWithLineAndArrivalTime(it.first, it.second, null) }
                .sortedBy { it.line.number })
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )

    /**
     * Indicates that we want to update the arrival data.
     */
    private val _wantUpdate = MutableStateFlow(false)

    /**
     * Map that contains arrival times for the routes passing through the current stop.
     */
    private val _arrivalTimes = combineTransform(_stop, _wantUpdate) { stop, want ->
        if (want) {
            try {
                emit(liveDataRepository.getStopArrivals(stop.stopId))
                _wantUpdate.value = false
            } catch (e: Exception) {
                TODO("Network error")
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyMap()
    )

    /**
     * Contains all lines passing through the stop with the latest arrival times. It's updated whenever we get new routes
     * or new arrival times.
     */
    val routesWithLineAndArrivalTimes = combineTransform(_arrivalTimes, _routesWithLines) { arrivalTimes, routes ->
        val updated = routes.map { RouteWithLineAndArrivalTime(it.route, it.line, arrivalTimes[it.route.routeId]) }
            .sortedWith(compareBy(nullsLast()) { it.arrivalTimes?.min() })
        emit(updated)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )

    /**
     * Resets the ViewModel fields to prevent conflicts when changing stops.
     */
    private fun clear(){
        _wantUpdate.value = true
    }

    /**
     * Select a new stop for the ViewModel
     */
    fun setStop(s: Stop){
        viewModelScope.launch {
            clear()
            _stop.emit(s)
        }
    }
}