package xyz.teogramm.thessalonikitransit.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import xyz.teogramm.oasth.OasthLive
import javax.inject.Inject

class LiveDataRepository @Inject constructor() {
    /**
     * @return Map matching each routeId to the arrival time
     */
    suspend fun getStopArrivals(stopId: Int): Map<Int,Int> {
        return withContext(Dispatchers.IO) {
            try {
                val arrivals = OasthLive.getStopArrivals(stopId)
                val arrivalsMap = HashMap<Int, Int>()
                arrivals.forEach { arrival ->
                    arrivalsMap[arrival.routeCode] = arrival.estimatedTime
                }
                return@withContext arrivalsMap
            } catch (e: Throwable){
                return@withContext mapOf()
            }
        }
    }
}