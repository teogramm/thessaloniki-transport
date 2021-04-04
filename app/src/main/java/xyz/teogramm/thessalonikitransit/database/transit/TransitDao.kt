package xyz.teogramm.thessalonikitransit.database.transit

import androidx.room.*
import xyz.teogramm.thessalonikitransit.database.transit.entities.*

@Dao
interface TransitDao {
    /*
     * Insertion
     *
     * All insertion operations have OnConflictStrategy.IGNORE, because when populating the database I simply insert
     * each new route or stop I encounter, instead of checking if it exists and then adding it. This is probably
     * harmless for objects fetched through the API.
     */

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addStops(vararg stops: Stop)

    @Insert
    fun addRouteStop(routeStop: RouteStop)

    @Transaction
    @Insert
    fun addRouteStops(vararg routeStops: RouteStop)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addRoute(route: Route)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addLine(line: Line)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addMasterLine(masterLine: MasterLine)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addCalendar(calendar: Calendar)

    @Insert
    fun addSchedule(schedule: Schedule): Long

    @Transaction
    @Insert
    fun addScheduleTimes(vararg scheduleTimes: ScheduleTime)

    @Insert
    fun addScheduleTime(scheduleTime: ScheduleTime)

    @Query("SELECT scheduleId FROM Schedule WHERE rowid = :rowId")
    fun getScheduleIdFromRowId(rowId: Long): Long

    /*
     * Queries
     */

    /**
     * Gets all the stops of a route, in ascending order.
     */
    @Transaction
    @Query("SELECT Stop.* FROM RouteStop INNER JOIN Stop ON RouteStop.stopId = Stop.stopId WHERE " +
            "RouteStop.routeId = :routeId ORDER BY RouteStop.stopIndex")
    fun getRouteStopsOrdered(routeId: Int): List<Stop>

    @Transaction
    @Query("SELECT * FROM MasterLine")
    fun getMasterLinesWithLines(): List<MasterLineWithLines>

    /**
     * Gets all lines included in the database and the routes associated with each one.
     */
    @Transaction
    @Query("SELECT * FROM Line ORDER BY Line.number")
    fun getAllLinesWithRoutes(): List<LineWithRoutes>

    /**
     * Gets a list of [ScheduleWithTimes] objects for the given [lineId].
     */
    @Transaction
    @Query("SELECT * FROM Schedule WHERE lineId = :lineId")
    fun getLineSchedulesWithTimes(lineId: Int): List<ScheduleWithTimes>

    @Transaction
    @Query("SELECT * FROM StopsWithRoutesAndLines WHERE stopId = :stopId")
    fun getLinesForStop(stopId: Int): List<StopsWithRoutesAndLines>

}
