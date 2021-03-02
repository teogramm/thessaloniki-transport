package xyz.teogramm.thessalonikitransit.database.transit

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import xyz.teogramm.oasth.OasthData
import xyz.teogramm.oasth.base.BusRoute
import xyz.teogramm.oasth.base.RouteTypes
import xyz.teogramm.oasth.base.schedules.BusSchedule
import xyz.teogramm.thessalonikitransit.R
import xyz.teogramm.thessalonikitransit.database.transit.entities.*

/**
 * Object containing methods for adding static data
 */
object DatabaseInitializer {
    /**
     * Adds the given data to the database by using the given DAO.
     */
    suspend fun populateTransitDatabase(dao: TransitDao, data: OasthData) {
        // Create a coroutine scope for this function
        val coroutineScope = CoroutineScope(currentCoroutineContext())
        Log.d("DatabaseInitializer","Database initialisation started.")
        data.masterLines.values.forEach { masterLine ->
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    dao.addMasterLine(
                        MasterLine(
                            masterLine.internalId,
                            masterLine.number,
                            masterLine.nameEL,
                            masterLine.nameEL
                        )
                    )
                }
                // Launch a new thread for each masterline
                masterLine.lines.forEach { line ->
                    withContext(Dispatchers.IO) {
                        dao.addLine(
                            Line(line.internalId, line.number, line.nameEL, line.nameEN, masterLine.internalId)
                        )
                    }
                    line.routes.forEach { route ->
                        addRoute(dao, line.internalId, route)
                    }
                    line.schedules.forEach {
                        addSchedule(dao, line.internalId, it)
                    }
                }
            }
        }
        Log.d("DatabaseInitializer","Database initialization finished.")
    }

    /**
     * Adds the given route to a database using the given [TransitDao]. It adds all the stops of the route
     * separately to the database and then creates the [RouteStop] objects.
     */
    private suspend fun addRoute(dao: TransitDao, lineId: Int, route: BusRoute) {
        dao.addRoute(Route(route.internalId, route.nameEL, route.nameEN, route.type.id, lineId))
        // Create a Stop object for each stop of the route
        val stops = route.Stops.map { stop ->
            Stop(
                stop.internalId,
                stop.publicId,
                stop.nameEL,
                stop.nameEN,
                stop.heading,
                stop.longitude,
                stop.latitude
            )
        }
        withContext(Dispatchers.IO) {
            dao.addStops(*stops.toTypedArray())
        }
        val routeStops = route.Stops.mapIndexed { index, busStop ->
            RouteStop(route.internalId, busStop.internalId, index)
        }
        withContext(Dispatchers.IO) {
            dao.addRouteStops(*routeStops.toTypedArray())
        }
    }

    /**
     * Adds the given schedule and the associated calendar to the database using the given [TransitDao].
     */
    private suspend fun addSchedule(dao: TransitDao, lineId: Int, schedule: BusSchedule) {
        withContext(Dispatchers.IO) {
            val calendar = schedule.calendar
            dao.addCalendar(Calendar(calendar.id, calendar.nameEL, calendar.nameEN))
            // Schedule ID is automatically generated when adding the schedule to the database
            val schRowId = dao.addSchedule(Schedule(null, lineId, calendar.id))
            val schId = dao.getScheduleIdFromRowId(schRowId)
            // Map each entry in the inboundTimes and outboundTimes maps to a ScheduleTime object and add it to
            // the database.
            val scheduleInboundTimes =
                schedule.inboundTimes.map { ScheduleTime(schId, ScheduleEntryDirection.RETURN, it) }
            dao.addScheduleTimes(*scheduleInboundTimes.toTypedArray())
            val scheduleOutboundTimes =
                schedule.outboundTimes.map { ScheduleTime(schId, ScheduleEntryDirection.OUTBOUND, it) }
            dao.addScheduleTimes(*scheduleOutboundTimes.toTypedArray())
        }
    }

    /**
     * Returns whether the database is initialised by checking a shared preferences value
     */
    fun isDbInitialized(context: Context?): Boolean {
        val sharedPref =
            context?.getSharedPreferences(context.getString(R.string.preferenceFileName), Context.MODE_PRIVATE)
        // For database to have been initialized successfully the key must be present in shared preferences and its
        // value must be true.
        return sharedPref?.getBoolean(context.getString(R.string.preferenceDbInitializedKey),false) ?: false
    }

    /**
     * Sets the db initialized key to the given value. If no value is given, true is used.
     */
    fun setDbInitialized(context: Context?, value: Boolean = true) {
        val sharedPref =
            context?.getSharedPreferences(context.getString(R.string.preferenceFileName), Context.MODE_PRIVATE)
        if (sharedPref != null) {
            with(sharedPref.edit()){
                putBoolean(context.getString(R.string.preferenceDbInitializedKey), value)
                apply()
            }
        }
    }
}
