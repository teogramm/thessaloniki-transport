package xyz.teogramm.thessalonikitransit.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import xyz.teogramm.oasth.OasthLive

class LiveDataRepository {
    /**
     * @return Map matching each routeId to the arrival time
     */
    suspend fun getStopArrivals(stopId: Int): Map<Int,Int> {
        val arrivals = OasthLive.getStopArrivals(stopId)
        val arrivalsMap = HashMap<Int,Int>()
        arrivals.forEach { arrival ->
            arrivalsMap[arrival.routeCode] = arrival.estimatedTime
        }
        return arrivalsMap
    }
}