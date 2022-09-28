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
     * When [routesWithLineAndArrivalTimes] starts to be observed it activates [_routesWithLines] and [_wantUpdate].
     * Until these flows get new values, it uses their cached values. However, if a different stop was set in
     * [setStop], that data is obsolete and this is reflected in the UI by an annoying brief flash.
     *
     * By using this variable we can ensure that when a different stop is set, we wait for the routes to be fetched from
     * the database before they are exposed. This is achieved by [setStop] setting it to false if the new stop is
     * different from the one in the ViewModel and [_routesWithLines] setting it to true after it emits the routes
     * for the new stop.
     */
    private val _readyForNewStop = MutableStateFlow(false)

    /**
     * This initially fetches lines and routes from the database. Emits [RouteWithLineAndArrivalTime] objects, without
     * populating the arrival time field.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _routesWithLines = _stop.transformLatest { stop ->
        val routesWithLines = withContext(Dispatchers.IO) {
            staticRepository.getRoutesWithLinesForStop(stop)
        }
        emit(routesWithLines.map { RouteWithLineAndArrivalTime(it.first, it.second, null) }
                .sortedBy { it.line.number })
        _readyForNewStop.value = true
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
    private val _arrivalTimes = combineTransform(_stop,_wantUpdate) { stop, want ->
        if (want) {
            try {
                emit(liveDataRepository.getStopArrivals(stop.stopId))
                _wantUpdate.value = false
            } catch (e: Exception) {
//                TODO("Network error")
                emit(emptyMap())
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
    val routesWithLineAndArrivalTimes = combineTransform(_arrivalTimes, _routesWithLines, _readyForNewStop) { arrivalTimes, routes, ready ->
        if(ready) {
            val updated = routes.map { RouteWithLineAndArrivalTime(it.route, it.line, arrivalTimes[it.route.routeId]) }
                .sortedWith(compareBy(nullsLast()) { it.arrivalTimes?.min() })
            emit(updated)
        }else {
            emit(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        // Disable caching on all flows as it causes the UI to briefly display the previously cached routes when
        // changing stops. Enabling caching on either one of the other flows causes [routesWithLineAndArrivalTimes]
        // to emit the previous values. This happens because when the StopDetailsFragment subscribes to the flow,
        // it immediately posts a new value using the cached values of the other flows.
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
        clear()
        // Check if the selected stop is different from the existing one in the ViewModel
        if(s != _stop.value){
            _readyForNewStop.value = false
        }
        viewModelScope.launch {
            _stop.emit(s)
        }
    }
}