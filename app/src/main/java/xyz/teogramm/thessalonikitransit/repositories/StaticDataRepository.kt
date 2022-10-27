package xyz.teogramm.thessalonikitransit.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import xyz.teogramm.oasth.Oasth
import xyz.teogramm.thessalonikitransit.database.transit.DatabaseInitializer
import xyz.teogramm.thessalonikitransit.database.transit.TransitDatabase
import xyz.teogramm.thessalonikitransit.database.transit.entities.*
import javax.inject.Inject

/**
 * Repository for getting static transit information from the database.
 */
class StaticDataRepository @Inject constructor(
    private val transitDatabase: TransitDatabase
) {
    private val transitDao = transitDatabase.transitDao()

    /**
     * Downloads static data from the OASTH API and adds it to the database
     */
    suspend fun downloadAndInitializeDB() {
        val o = Oasth()
        val data = o.fetchData()
        withContext(Dispatchers.IO) {
            transitDatabase.clearAllTables()
            DatabaseInitializer.populateTransitDatabase(transitDao, data)
        }
    }


    suspend fun getAllLinesRoutesWithLastStops() : List<LineWithRoutes> = withContext(Dispatchers.IO) {
        return@withContext transitDao.getAllLinesWithRoutes()
    }

    suspend fun getAllStopsForRoute(route: Route): List<Stop> = withContext(Dispatchers.IO) {
        return@withContext transitDao.getRouteStopsOrdered(route.routeId)
    }

    suspend fun getLineSchedulesForDirection(line: Line, direction: ScheduleEntryDirection): List<ScheduleWithTimes> =
        withContext(Dispatchers.IO) {
            val schedulesWithAllTimes = transitDao.getLineSchedulesWithTimes(line.lineId)
            return@withContext schedulesWithAllTimes.map{ scheduleWithTimes ->
                // For each schedule create a copy containing only times towards the given direction
                scheduleWithTimes.copy(times = scheduleWithTimes.times.filter { it.direction == direction })
            }
        }

    /**
     * Returns the routes that pass through the stop with the given stopId along with the lines they belong to.
     */
    suspend fun getRoutesWithLinesForStop(stop: Stop): List<Pair<Route,Line>> = getRoutesWithLinesForStop(stop.stopId)

    suspend fun getRoutesWithLinesForStop(stopId: Int): List<Pair<Route,Line>> = withContext(Dispatchers.IO) {
        return@withContext transitDao.getLinesForStop(stopId).map {
            // Create a new route with the returned information
            Pair(Route(it.routeId,it.routeNameEL,it.routeNameEN,it.routeType,it.line.lineId),it.line)
        }
    }
}
