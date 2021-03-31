package xyz.teogramm.thessalonikitransit.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import xyz.teogramm.thessalonikitransit.database.transit.entities.Line
import xyz.teogramm.thessalonikitransit.database.transit.entities.Stop
import xyz.teogramm.thessalonikitransit.repositories.StaticDataRepository
import javax.inject.Inject

@HiltViewModel
class StopViewModel @Inject constructor(private val staticRepository: StaticDataRepository): ViewModel() {
    private val stopLines = MutableLiveData<List<Line>>()

    fun setStop(stop: Stop) {
        viewModelScope.launch {
            stopLines.postValue(staticRepository.getLinesForStop(stop))
        }
    }

    fun getStopLines(): LiveData<List<Line>> {
        return stopLines
    }
}

