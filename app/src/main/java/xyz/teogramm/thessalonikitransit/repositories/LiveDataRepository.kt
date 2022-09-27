package xyz.teogramm.thessalonikitransit.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import xyz.teogramm.oasth.OasthLive
import xyz.teogramm.oasth.live.Coordinates
import javax.inject.Inject

class LiveDataRepository @Inject constructor() {
    /**
     * @return Map matching each routeId to the arrival time
     */
    suspend fun getStopArrivals(stopId: Int): Map<Int,List<Int>> {
        return withContext(Dispatchers.IO) {
            try {
                val arrivals = OasthLive.getStopArrivals(stopId)
                val arrivalsMap = hashMapOf<Int,MutableList<Int>>()
                arrivals.forEach { arrival ->
                    val list = arrivalsMap.getOrDefault(arrival.routeCode, mutableListOf())
                    list.add(arrival.estimatedTime)
                    arrivalsMap[arrival.routeCode] = list
                }
                return@withContext arrivalsMap
            } catch (e: Throwable){
                return@withContext mapOf()
            }
        }
    }

    suspend fun getRoutePoints(routeId: Int): List<Coordinates> {
        return withContext(Dispatchers.IO) {
            return@withContext OasthLive.getRoutePoints(routeId)
        }
    }
}