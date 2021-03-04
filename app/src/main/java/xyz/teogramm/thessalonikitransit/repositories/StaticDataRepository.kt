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

    /**
     * Returns a list of all lines with their routes.
     */
    suspend fun getAllLines(): List<LineWithRoutes> = withContext(Dispatchers.IO) {
        return@withContext transitDao.getAllLinesWithRoutes()
    }

    suspend fun getAllLinesRoutesWithLastStops() : List<LineWithRoutes> = withContext(Dispatchers.IO) {
        return@withContext transitDao.getAllLinesWithRoutes()
    }

    suspend fun getAllStopsForRoute(route: Route): List<Stop> = withContext(Dispatchers.IO) {
        return@withContext transitDao.getRouteStopsOrdered(route.routeId)
    }

    suspend fun getLineSchedules(line: Line): List<ScheduleWithCalendarAndTimes> = withContext(Dispatchers.IO) {
        return@withContext transitDao.getLineSchedulesWithTimes(line.lineId)
    }
}
