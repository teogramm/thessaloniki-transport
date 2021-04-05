package xyz.teogramm.thessalonikitransit.viewModels

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import xyz.teogramm.thessalonikitransit.database.transit.entities.Line
import xyz.teogramm.thessalonikitransit.database.transit.entities.Stop
import xyz.teogramm.thessalonikitransit.repositories.LiveDataRepository
import xyz.teogramm.thessalonikitransit.repositories.StaticDataRepository
import javax.inject.Inject

@HiltViewModel
class StopViewModel @Inject constructor(private val staticRepository: StaticDataRepository,
                                        private val liveDataRepository: LiveDataRepository): ViewModel() {
    private val stop = MutableLiveData<Stop>()
    // Stores the lines passing through this stop. Is not exposed to other classes.
    private var lines = emptyList<Line>()
    // Matches each routeId to the corresponding line. Is used because the API returns routeIds.
    private var routeIdsToLines = HashMap<Int,Line>()
    // Contains all lines passing through the stop and in addition
    private val stopLines = MutableLiveData<List<LineWithArrivalTime>>()


    fun setStop(aStop: Stop) {
        // Cancel any previous jobs.
        routeIdsToLines.clear()
        stop.value = aStop
        viewModelScope.launch {
            // Assign routes to lines
            // Create a new LineWithArrivalTime object for each line passing through the stop
            val routesWithLines = async {
                staticRepository.getRoutesWithLinesForStop(aStop)
            }
            val newStopLines = mutableListOf<LineWithArrivalTime>()
            routesWithLines.await().forEach{ routeLinePair ->
                routeIdsToLines[routeLinePair.first.routeId] = routeLinePair.second
                newStopLines.add(LineWithArrivalTime(routeLinePair.second))
            }
            stopLines.value = newStopLines
            // Update the times
            updateTimes()
        }
    }

    fun getStop(): LiveData<Stop>{
        return stop
    }

    fun getStopLinesWithArrivalTimes(): LiveData<List<LineWithArrivalTime>> {
        return stopLines
    }

    // TODO: Improve code quality.
    fun updateTimes() {
        viewModelScope.launch {
            // Get times from the API
            val times = liveDataRepository.getStopArrivals(stop.value!!.stopId)
            times.forEach { routeId, arrivalTime ->
                val line = routeIdsToLines[routeId]!!
                stopLines.value?.find { it.line.lineId == line.lineId }!!.setArrivalTimes(listOf(arrivalTime))
            }
        }
    }
}

/**
 * Contains information about a line and a its estimated arrival time.
 */
class LineWithArrivalTime(val line: Line){
    private val arrivalTime = MutableLiveData<List<Int>>()

    fun setArrivalTimes(newTime: List<Int>) {
        arrivalTime.value = newTime
    }

    /**
     * Get a LiveData object for the arrival times of a line.
     */
    fun getArrivalTimes(): LiveData<List<Int>> {
        return arrivalTime
    }
}