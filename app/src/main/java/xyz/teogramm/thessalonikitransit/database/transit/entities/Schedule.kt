package xyz.teogramm.thessalonikitransit.database.transit.entities

import androidx.room.*
import xyz.teogramm.thessalonikitransit.database.transit.TransitConverters
import java.time.LocalTime

/**
 * A schedule is a set of departure times for a line, for both outbound and return routes, associated with a calendar.
 */
@Entity(
    foreignKeys = [
        ForeignKey(entity = Line::class, parentColumns = ["lineId"], childColumns = ["lineId"]),
        ForeignKey(entity = Calendar::class, parentColumns = ["calendarId"], childColumns = ["calendarId"])
    ],
    indices = [
        // Used when searching for all scheduleIds for a line
        Index(value = ["lineId"])
    ]
)
data class Schedule(
    @PrimaryKey(autoGenerate = true) val scheduleId: Int?,
    val lineId: Int,
    val calendarId: Int
)

/**
 * A departure time for a given schedule, in a specific direction.
 */
@Entity(
    primaryKeys = ["scheduleId", "direction", "time"],
    foreignKeys = [
        ForeignKey(entity = Schedule::class, parentColumns = ["scheduleId"], childColumns = ["scheduleId"])
    ],
    indices = [
        // Used when searching for all departure times associated with a scheduleId, with or without the
        // direction.
        Index(value = ["scheduleId","direction"])
    ]
)
data class ScheduleTime(
    val scheduleId: Long,
    val direction: ScheduleEntryDirection,
    val time: LocalTime
)

/**
 * Indicates the direction of a schedule entry.
 * Circular routes have only outbound times
 */
enum class ScheduleEntryDirection{
    OUTBOUND, RETURN
}

/**
 * A schedule along with calendar information and a list of [ScheduleTime] objects that are included in the schedule.
 */
data class ScheduleWithTimes(
    @Embedded val schedule: Schedule,
    @Relation(
        parentColumn = "calendarId",
        entityColumn = "calendarId"
    )
    val calendar: Calendar,
    @Relation(
        parentColumn = "scheduleId",
        entityColumn = "scheduleId"
    )
    val times: List<ScheduleTime>
)
