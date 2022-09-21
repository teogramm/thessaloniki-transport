package xyz.teogramm.thessalonikitransit.viewModels

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import xyz.teogramm.thessalonikitransit.database.transit.entities.Line
import xyz.teogramm.thessalonikitransit.database.transit.entities.Stop
import xyz.teogramm.thessalonikitransit.repositories.LiveDataRepository
import xyz.teogramm.thessalonikitransit.repositories.StaticDataRepository
import javax.inject.Inject

@HiltViewModel
class StopViewModel @Inject constructor(private val staticRepository: StaticDataRepository,
                                        private val liveDataRepository: LiveDataRepository): ViewModel() {

    // Use a dummy stop as the initial value
    private val _stop = MutableStateFlow(Stop(0,"","","",0,0.0,0.0))

    /**
     * Contains the currently selected stop
     */
    val stop = _stop.asStateFlow()


    private val _routeIdsToLines = MutableStateFlow<Map<Int,Line>>(emptyMap())
    /**
     * Matches each routeId passing through this stop to its line
     */
    val routeIdsToLines: StateFlow<Map<Int,Line>> = _routeIdsToLines

    private val _wantUpdate = MutableStateFlow(false)

    /**
     * Maps each lineId to its arrival time. This is always updated after [routeIdsToLines] to ensure each lineId
     * key can be assigned to a route.
     */
    val lineArrivalTimes = _wantUpdate.transform{aWantUpdate->
        // Get times from the API
        if(aWantUpdate) {
            val times = liveDataRepository.getStopArrivals(_stop.value.stopId)
            emit(times.mapKeys { routeId -> routeIdsToLines.value[routeId.key]!!.lineId })
            _wantUpdate.value = false
        }
    }
    init {
        viewModelScope.launch {
            // When the stop is updated
            _stop.collectLatest {newStop ->
                // Clear the previous values before populating new ones
                clear()
                val routesWithLines = withContext(Dispatchers.IO) {
                    staticRepository.getRoutesWithLinesForStop(newStop)
                }
                _routeIdsToLines.emit(routesWithLines.associate { pair -> Pair(pair.first.routeId, pair.second) })
                _wantUpdate.value = true
            }
        }
    }

    /**
     * Resets the ViewModel fields to prevent conflicts when changing stops.
     */
    private fun clear(){
        _wantUpdate.value = false
        _routeIdsToLines.value = emptyMap()
    }

    /**
     * Select a new stop for the ViewModel
     */
    fun setStop(s: Stop){
        viewModelScope.launch {
            _stop.emit(s)
        }
    }

    /**
     * Gets updated times for [lineArrivalTimes]
     */
    fun updateTimes() {
        _wantUpdate.value = true
    }
}