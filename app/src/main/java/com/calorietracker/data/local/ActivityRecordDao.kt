package com.calorietracker.data.local

import androidx.room.*
import com.calorietracker.data.model.ActivityRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityRecordDao {

    @Query("SELECT * FROM activity_records WHERE userId = :userId ORDER BY date DESC")
    fun getAllRecords(userId: String): Flow<List<ActivityRecord>>

    @Query("SELECT * FROM activity_records WHERE userId = :userId AND date(date / 1000, 'unixepoch') = date('now')")
    fun getTodayRecord(userId: String): Flow<ActivityRecord?>

    @Query("SELECT * FROM activity_records WHERE id = :id")
    suspend fun getRecordById(id: Long): ActivityRecord?

    @Query("SELECT SUM(steps) FROM activity_records WHERE userId = :userId AND date(date / 1000, 'unixepoch') = date('now')")
    suspend fun getTodaySteps(userId: String): Long?

    @Query("SELECT SUM(caloriesBurned) FROM activity_records WHERE userId = :userId AND date(date / 1000, 'unixepoch') = date('now')")
    suspend fun getTodayCaloriesBurned(userId: String): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: ActivityRecord): Long

    @Update
    suspend fun updateRecord(record: ActivityRecord)

    @Delete
    suspend fun deleteRecord(record: ActivityRecord)

    @Query("DELETE FROM activity_records WHERE userId = :userId")
    suspend fun deleteAllUserRecords(userId: String)
}
