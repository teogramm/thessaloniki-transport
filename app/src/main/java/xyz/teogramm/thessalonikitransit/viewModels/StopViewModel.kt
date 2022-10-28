package xyz.teogramm.thessalonikitransit.viewModels

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import xyz.teogramm.thessalonikitransit.database.transit.alerts.StopWithNotificationThreshold
import xyz.teogramm.thessalonikitransit.database.transit.entities.Line
import xyz.teogramm.thessalonikitransit.database.transit.entities.Stop
import xyz.teogramm.thessalonikitransit.fragments.alerts.CreateAlertDialogUiState
import xyz.teogramm.thessalonikitransit.fragments.stopDetails.RouteWithLineAndArrivalTime
import xyz.teogramm.thessalonikitransit.repositories.AlertsRepository
import xyz.teogramm.thessalonikitransit.repositories.LiveDataRepository
import xyz.teogramm.thessalonikitransit.repositories.StaticDataRepository
import javax.inject.Inject

/**
 * ViewModel containing information about a specific stop
 */
@HiltViewModel
class StopViewModel @Inject constructor(private val staticRepository: StaticDataRepository,
                                        private val liveDataRepository: LiveDataRepository,
                                        private val alertsRepository: AlertsRepository): ViewModel() {

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
        // We always want to fetch the latest routes with lines for the stop so start this eagerly.
        // Also alertDialogUiState depends on this being set to Eagerly since it uses its value without collecting.
        started = SharingStarted.Eagerly,
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
        // Do not cache the arrival times
        started = SharingStarted.WhileSubscribed(0,0),
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

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _stopAlerts = _stop.flatMapLatest {stop ->
        // Transform the stop flow into a flow that emits CompleteAlert objects for this stop from the database
        alertsRepository.getStopAlerts(stop.stopId)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = null
    )

    val alertDialogUiState = _stopAlerts.combineTransform(_readyForNewStop) { stopAlerts, ready ->
        if(stopAlerts != null && ready) {
            // Each stop has at most one threshold entry
            val notificationThreshold = stopAlerts.stop.notificationThreshold.firstOrNull()
            // Get all the lines passing through this stop. Since multiple routes might correspond to a single line
            // make sure to remove duplicate line entries
            val lines = _routesWithLines.first().map { it.line }.distinct()
            // Get all lines that have alerts enabled for their routes. Set automatically de-duplicates.
            val enabledLines = stopAlerts.routes.map { it.line }.toSet()
            val uiState = CreateAlertDialogUiState(notificationThreshold, lines, enabledLines)
            emit(uiState)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = null
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

    fun setStopAlerts( enabledLines: Set<Line>, notificationThreshold: Int){
        // Get stopId synchronously
        val currentStopId = _stop.value.stopId
        viewModelScope.launch {
            withContext(NonCancellable) {
                val routeIds = enabledLines.flatMap { line ->
                    // Get the corresponding routes (might be one or more) for this line
                    _routesWithLines.value.filter { it.line.lineId == line.lineId }.map { it.route.routeId }
                }
                alertsRepository.addAlert(currentStopId, routeIds, notificationThreshold)
            }
        }
    }

    /**
     * Delete threshold time and all alerts for the current stop
     */
    fun deleteStopAlerts(){
        // Get stopId synchronously
        val currentStopId = _stop.value.stopId
        viewModelScope.launch {
            alertsRepository.deleteAlerts(currentStopId)
        }
    }
}