package xyz.teogramm.thessalonikitransit.database.transit.alerts

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import xyz.teogramm.thessalonikitransit.database.transit.entities.RouteWithLine

@Dao
interface AlertDao {

    // We don't care about the user adding alerts already in the database
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addAlerts(alerts: List<Alert>)

    /**
     * Adds a threshold value for the given stop. If a threshold exists for the stop, it replaces it.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addStopThreshold(notificationThreshold: StopNotificationTime)

    // Use exists to only get stops which have an alert. (Otherwise we get all stops)
    @Transaction
    @Query("SELECT * FROM stop WHERE EXISTS(SELECT * FROM Alert WHERE Alert.stopId = stop.stopId)")
    fun getAllAlerts(): Flow<List<CompleteAlert>>

    @Query("DELETE FROM StopNotificationTime WHERE stopId = :stopId")
    fun deleteNotificationTime(stopId: Int)

    @Query("DELETE FROM Alert WHERE stopId = :stopId")
    fun deleteStopAlerts(stopId: Int)
}