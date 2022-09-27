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
    private val _stop = MutableStateFlow(Stop(0,"","","",0,0.0,0.0))

    /**
     * Contains the currently selected stop
     */
    val stop = _stop.asStateFlow()

    /**
     * Indicates that the line data has been fetched from the static repository and arrival data can now be fetched.
     */
    private var readyToFetchArrivalTimes = MutableStateFlow(false)

    /**
     * This initially fetches lines and routes from the database. It emits its value so the UI can be populated and
     * then sets [readyToFetchArrivalTimes] to true, so arrival times can be fetched.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _routesWithLines = _stop.transformLatest { stop ->
        viewModelScope.launch {
            val routesWithLines = withContext(Dispatchers.IO){
                staticRepository.getRoutesWithLinesForStop(stop)
            }
            emit(routesWithLines.map { RouteWithLineAndArrivalTime(it.first, it.second, null) }
                                        .sortedBy { it.line.number })
            readyToFetchArrivalTimes.value = true
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
     * This runs whenever [_wantUpdate], [readyToFetchArrivalTimes] or [_routesWithLines] are modified but only
     * if [_wantUpdate] and [readyToFetchArrivalTimes] are both true, so we can ensure that the correct lines
     * have been loaded from the database first.
     */
    private val _routesWithLineArrivalTimes = combineTransform(_wantUpdate, readyToFetchArrivalTimes, _routesWithLines)
    { want, ready, routes ->
        if (want and ready) {
            val arrivalTimes = withContext(Dispatchers.IO) {
                liveDataRepository.getStopArrivals(stop.value.stopId)
            }
            val updated = routes.map { RouteWithLineAndArrivalTime(it.route,it.line, arrivalTimes[it.route.routeId]) }
                .sortedWith(
                    compareBy ( nullsLast()) {
                    it.arrivalTimes?.min()
                })
            emit(updated)
            _wantUpdate.value = false
        }
    }

    @OptIn(FlowPreview::class)
    val routesWithLineAndArrivalTime = flowOf(_routesWithLines, _routesWithLineArrivalTimes).flattenMerge()

    /**
     * Resets the ViewModel fields to prevent conflicts when changing stops.
     */
    private fun clear(){
        readyToFetchArrivalTimes.value = false
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