package xyz.teogramm.thessalonikitransit.viewModels

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import xyz.teogramm.thessalonikitransit.database.transit.entities.Line
import xyz.teogramm.thessalonikitransit.database.transit.entities.Route
import xyz.teogramm.thessalonikitransit.database.transit.entities.ScheduleWithCalendarAndTimes
import xyz.teogramm.thessalonikitransit.database.transit.entities.Stop
import xyz.teogramm.thessalonikitransit.repositories.StaticDataRepository
import javax.inject.Inject

@HiltViewModel
class RouteViewModel @Inject constructor(
    private val staticRepository: StaticDataRepository
): ViewModel(){
    private val selectedLine = MutableLiveData<Line>()
    private val selectedRoute = MutableLiveData<Route>()
    val stops = MutableLiveData<List<Stop>>()
    val schedules = MutableLiveData<List<ScheduleWithCalendarAndTimes>>()
    /**
     * Updates the stops list whenever the route is updated
     */
    private val lineObserver = Observer<Line> {
        viewModelScope.launch {
            schedules.value = staticRepository.getLineSchedules(it)
        }
    }
    private val routeObserver = Observer<Route> {
        viewModelScope.launch {
            stops.value = staticRepository.getAllStopsForRoute(it)
        }
    }

    init {
        // Register the observers
        selectedLine.observeForever(lineObserver)
        selectedRoute.observeForever(routeObserver)
    }

    fun setSelected(line: Line, route: Route) {
        selectedLine.value = line
        selectedRoute.value = route
    }

    fun getSelectedLineNumber(): String{
        return selectedLine.value?.number ?: ""
    }

    fun getSelectedRouteName(): String {
        return selectedRoute.value?.nameEL ?: ""
    }

    override fun onCleared() {
        selectedLine.removeObserver(lineObserver)
        selectedRoute.removeObserver(routeObserver)
        super.onCleared()
    }
}