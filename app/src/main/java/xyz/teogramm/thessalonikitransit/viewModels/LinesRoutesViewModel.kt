package xyz.teogramm.thessalonikitransit.viewModels

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import xyz.teogramm.thessalonikitransit.database.transit.entities.LineWithRoutes
import xyz.teogramm.thessalonikitransit.repositories.StaticDataRepository
import javax.inject.Inject;

/**
 * ViewModel for displaying all lines and their routes. Is meant to be used with
 * [xyz.teogramm.thessalonikitransit.fragments.LineDisplayFragment].
 */
@HiltViewModel
class LinesRoutesViewModel @Inject constructor(private val staticRepository: StaticDataRepository): ViewModel() {

    private val linesWithRoutes: LiveData<List<LineWithRoutes>> = liveData {
        val data = fetchLinesWithRoutes()
        emit(data)
    }

    fun getLinesWithRoutes(): LiveData<List<LineWithRoutes>>{
        return linesWithRoutes
    }

    private suspend fun fetchLinesWithRoutes(): List<LineWithRoutes> {
        var lines: List<LineWithRoutes>
        withContext(Dispatchers.IO){
            lines = staticRepository.getAllLinesRoutesWithLastStops()
        }
        return lines
    }
}