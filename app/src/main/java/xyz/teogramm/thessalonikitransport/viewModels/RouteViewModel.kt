package xyz.teogramm.thessalonikitransport.viewModels

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xyz.teogramm.thessalonikitransport.database.transit.entities.*
import xyz.teogramm.thessalonikitransport.repositories.LiveDataRepository
import xyz.teogramm.thessalonikitransport.repositories.StaticDataRepository
import java.time.LocalTime
import javax.inject.Inject

/**
 * ViewModel containing information about a specific route and the line it belongs to.
 * @see xyz.teogramm.thessalonikitransport.fragments.routeDetails
 */
@HiltViewModel
class RouteViewModel @Inject constructor(
    private val staticRepository: StaticDataRepository,
    private val liveRepository: LiveDataRepository
): ViewModel(){

    // Populate using dummy lines and routes
    private val _selectedLine = MutableStateFlow(Line(-500,"","","",-500))
    private val _selectedRoute = MutableStateFlow(Route(-500,"","",0,-500))
    val selectedLine = _selectedLine.asStateFlow()
    val selectedRoute = _selectedRoute.asStateFlow()

    private val _schedules = MutableStateFlow(emptyList<ScheduleWithGroupedTimes>())
    val schedules = _schedules.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val stops = selectedRoute.transformLatest{ newRoute ->
        emit(emptyList())
        emit(staticRepository.getAllStopsForRoute(newRoute))
    }.stateIn(
        initialValue = emptyList(),
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed()
    )

    // Maybe include route id to avoid redownloading. maybe find a way to cache route points.
    @OptIn(ExperimentalCoroutinesApi::class)
    val points = selectedRoute.transformLatest{ newRoute ->
        emit(emptyList())
        emit(liveRepository.getRoutePoints(newRoute.routeId).map { coordinates -> Pair(coordinates.latitude,coordinates.longitude) })
    }.stateIn(
        initialValue = emptyList(),
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed()
    )

    /**
     * Updates the view model with information about the given [Line] and [Route].
     */
    fun setSelected(line: Line, route: Route) {
        _selectedLine.value = line
        _selectedRoute.value = route
        // Do I/O in the background
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                // If route is outbound or circular get the outbound times, else get the return times.
                val direction = if (route.type == 2) {
                    ScheduleEntryDirection.RETURN
                } else {
                    ScheduleEntryDirection.OUTBOUND
                }
                val lineSchedules = staticRepository.getLineSchedulesForDirection(line, direction)
                _schedules.emit(getGroupedSchedules(lineSchedules))
            }
        }
    }

    /**
     * Converts a list of [ScheduleWithTimes] to a list of [ScheduleWithGroupedTimes].
     */
    private fun getGroupedSchedules(ungroupedSchedules: List<ScheduleWithTimes>): List<ScheduleWithGroupedTimes> {
        // For each schedule of a line, we want to group its times according to hour and then sort the times in
        // each group
        val groupedSchedules = mutableListOf<ScheduleWithGroupedTimes>()
        ungroupedSchedules.forEach{ schedule ->
            // When grouping, keep only the time field of each ScheduleTime, Then sort each time list
            val times = schedule.times.map { scheduleTimeEntry -> scheduleTimeEntry.time }.sorted()
            groupedSchedules.add(ScheduleWithGroupedTimes(schedule.schedule, schedule.calendar, times))
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
    val times: List<LocalTime>
)