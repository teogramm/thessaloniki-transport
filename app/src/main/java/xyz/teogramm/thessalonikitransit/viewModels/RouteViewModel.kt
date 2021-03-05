package xyz.teogramm.thessalonikitransit.viewModels

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xyz.teogramm.thessalonikitransit.database.transit.entities.*
import xyz.teogramm.thessalonikitransit.repositories.StaticDataRepository
import java.time.LocalTime
import javax.inject.Inject

/**
 * ViewModel containing information about a specific route and the line it belongs to.
 * @see xyz.teogramm.thessalonikitransit.fragments.routeDetails
 */
@HiltViewModel
class RouteViewModel @Inject constructor(
    private val staticRepository: StaticDataRepository
): ViewModel(){
    private val selectedLine = MutableLiveData<Line>()
    private val selectedRoute = MutableLiveData<Route>()
    val schedules = MutableLiveData<List<ScheduleWithGroupedTimes>>()
    val stops = MutableLiveData<List<Stop>>()

    /**
     * Updates the view model with information about the given [Line] and [Route].
     */
    fun setSelected(line: Line, route: Route) {
        selectedLine.value = line
        selectedRoute.value = route
        // Do I/O in the background
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                stops.postValue(staticRepository.getAllStopsForRoute(route))
                // If route is outbound or circular get the outbound times, else get the return times.
                val direction = if (route.type == 2) {
                    ScheduleEntryDirection.RETURN
                } else {
                    ScheduleEntryDirection.OUTBOUND
                }
                val lineSchedules = staticRepository.getLineSchedulesForDirection(line, direction)
                schedules.postValue(getGroupedSchedules(lineSchedules))
            }
        }
    }

    /**
     * @return Number of selected line, empty string if no line is selected.
     */
    fun getSelectedLineNumber(): String{
        return selectedLine.value?.number ?: ""
    }

    /**
     * @return Name of selected route, empty string if no route is selected.
     */
    fun getSelectedRouteName(): String {
        return selectedRoute.value?.nameEL ?: ""
    }

    /**
     * Converts a list of [ScheduleWithTimes] to a list of [ScheduleWithGroupedTimes].
     */
    private fun getGroupedSchedules(ungroupedSchedules: List<ScheduleWithTimes>): List<ScheduleWithGroupedTimes> {
        // For each schedule of a line, we want to group its times according to hour and then sort the times in
        // each group
        val groupedSchedules = mutableListOf<ScheduleWithGroupedTimes>()
        ungroupedSchedules.forEach{ schedule ->
            // When grouping, keep only the time field of each ScheduleTime
            val groupedTimes = schedule.times.map { scheduleTimeEntry -> scheduleTimeEntry.time }.groupBy { it.hour }
            // Then sort each time list
            val sortedGroupedTimes = groupedTimes.mapValues { it.value.sorted() }
            groupedSchedules.add(ScheduleWithGroupedTimes(schedule.schedule, schedule.calendar, sortedGroupedTimes))
        }
        return groupedSchedules.toList()
    }
}

/**
 * Schedule with times grouped by hour of day. Times for an hour are sorted in increasing order.
 */
data class ScheduleWithGroupedTimes(
    val schedule: Schedule,
    val calendar: Calendar,
    val groupedTimes: Map<Int,List<LocalTime>>
)