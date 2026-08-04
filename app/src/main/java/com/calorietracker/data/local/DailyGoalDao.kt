package com.calorietracker.data.local

import androidx.room.*
import com.calorietracker.data.model.DailyGoal
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyGoalDao {

    @Query("SELECT * FROM daily_goals WHERE userId = :userId")
    fun getGoalByUserId(userId: String): Flow<DailyGoal?>

    @Query("SELECT * FROM daily_goals WHERE userId = :userId")
    suspend fun getGoalByUserIdSync(userId: String): DailyGoal?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: DailyGoal): Long

    @Update
    suspend fun updateGoal(goal: DailyGoal)

    @Delete
    suspend fun deleteGoal(goal: DailyGoal)

    @Query("DELETE FROM daily_goals WHERE userId = :userId")
    suspend fun deleteGoalByUserId(userId: String)
}
