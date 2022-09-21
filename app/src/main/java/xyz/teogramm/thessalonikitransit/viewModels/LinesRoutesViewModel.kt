package xyz.teogramm.thessalonikitransit.viewModels

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xyz.teogramm.thessalonikitransit.database.transit.entities.LineWithRoutes
import xyz.teogramm.thessalonikitransit.repositories.StaticDataRepository
import javax.inject.Inject

/**
 * ViewModel holding information about all lines and their routes. Is meant to be used with
 * [xyz.teogramm.thessalonikitransit.fragments.lineDisplay.LineDisplayFragment].
 */
@HiltViewModel
class LinesRoutesViewModel @Inject constructor(private val staticRepository: StaticDataRepository): ViewModel() {
    private val _linesWithRoutes = MutableStateFlow<List<LineWithRoutes>>(emptyList())
    val linesWithRoutes: StateFlow<List<LineWithRoutes>> = _linesWithRoutes

    init {
        viewModelScope.launch {
            _linesWithRoutes.value = fetchLinesWithRoutes()
        }
    }

    /**
     * Gets all lines and their associated routes from the database.
     */
    private suspend fun fetchLinesWithRoutes(): List<LineWithRoutes> {
        var lines: List<LineWithRoutes>
        withContext(Dispatchers.IO){
            lines = staticRepository.getAllLinesRoutesWithLastStops()
        }
        return lines
    }
}