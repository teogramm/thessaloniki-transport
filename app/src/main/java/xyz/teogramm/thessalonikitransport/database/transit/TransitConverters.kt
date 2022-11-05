package xyz.teogramm.thessalonikitransport.database.transit

import androidx.room.TypeConverter
import xyz.teogramm.thessalonikitransport.database.transit.entities.ScheduleEntryDirection
import java.time.LocalTime

class TransitConverters {
    @TypeConverter
    fun fromScheduleEntryDirection(direction: ScheduleEntryDirection) = direction.ordinal

    @TypeConverter
    fun toScheduleEntryDirection(value: Int) = enumValues<ScheduleEntryDirection>()[value]

    @TypeConverter
    fun fromLocalTime(localTime: LocalTime) = localTime.toSecondOfDay()

    @TypeConverter
    fun toLocalTime(value: Long) = LocalTime.ofSecondOfDay(value)
}