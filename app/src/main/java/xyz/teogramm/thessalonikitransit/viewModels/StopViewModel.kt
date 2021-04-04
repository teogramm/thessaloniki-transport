package xyz.teogramm.thessalonikitransit.viewModels

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import xyz.teogramm.thessalonikitransit.database.transit.entities.Line
import xyz.teogramm.thessalonikitransit.database.transit.entities.Stop
import xyz.teogramm.thessalonikitransit.repositories.StaticDataRepository
import javax.inject.Inject

@HiltViewModel
class StopViewModel @Inject constructor(private val staticRepository: StaticDataRepository): ViewModel() {
    private val stop = MutableLiveData<Stop>()
    // Stores the lines passing through this stop. Is not exposed to other classes.
    private var lines = emptyList<Line>()
    // Matches each routeId to the corresponding line
    private var routeIdsToLines = HashMap<Int,Line>()
    // Contains all lines passing through the stop and in addition
    private val stopLines = MutableLiveData<List<LineWithArrivalTime>>()


    fun setStop(stop: Stop) {
        viewModelScope.launch {
            val routesWithLines = staticRepository.getRoutesWithLinesForStop(stop)
        }
    }

    fun getStop(): LiveData<Stop>{
        return stop
    }

    fun getStopLines(): LiveData<List<Line>> {
        return liveData {

        }
    }

    fun updateTimes() {

    }
}

/**
 * Contains information about a line and a its estimated arrival time.
 */
class LineWithArrivalTime(private val line: Line){
    private val arrivalTime = MutableLiveData<List<Int>>()

    fun setArrivalTimes(newTime: List<Int>) {
        arrivalTime.postValue(newTime)
    }

    /**
     * Get a LiveData object for the arrival times of a line.
     */
    fun getArrivalTimes(): LiveData<List<Int>> {
        return arrivalTime
    }
}