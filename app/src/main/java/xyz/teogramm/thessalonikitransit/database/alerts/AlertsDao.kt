package xyz.teogramm.thessalonikitransit.database.alerts

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapInfo
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AlertsDao {

    @Insert
    fun addUserAlerts(alerts: List<UserAlert>)

    @Query("DELETE FROM useralert WHERE stopId = :stopId")
    fun deleteUserAlert(stopId: Int)

    @Query("DELETE FROM stoptime WHERE stopId = :stopId")
    fun deleteNotificationTime(stopId: Int)

    @MapInfo(keyColumn = "stopId", valueColumn = "routeId")
    @Query("SELECT * FROM UserAlert GROUP BY stopId")
    fun getAllAlerts(): Map<Int, List<Int>>

    @Query("SELECT notificationTimeMinutes FROM stoptime WHERE stopId = :stopId")
    fun getStopNotificationTime(stopId: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addStopNotificationTime(stopTime: StopNotificationTime)
}