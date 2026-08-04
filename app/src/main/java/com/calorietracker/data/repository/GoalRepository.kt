package com.calorietracker.data.repository

import com.calorietracker.data.local.DailyGoalDao
import com.calorietracker.data.model.ActivityLevel
import com.calorietracker.data.model.DailyGoal
import com.calorietracker.data.model.Gender
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepository @Inject constructor(
    private val dailyGoalDao: DailyGoalDao
) {

    fun getGoalByUserId(userId: String): Flow<DailyGoal?> = 
        dailyGoalDao.getGoalByUserId(userId)

    suspend fun getGoalByUserIdSync(userId: String): DailyGoal? = 
        dailyGoalDao.getGoalByUserIdSync(userId)

    suspend fun saveGoal(goal: DailyGoal) {
        dailyGoalDao.insertGoal(goal)
    }

    suspend fun updateGoal(goal: DailyGoal) = 
        dailyGoalDao.updateGoal(goal)

    suspend fun deleteGoal(goal: DailyGoal) = 
        dailyGoalDao.deleteGoal(goal)

    suspend fun deleteGoalByUserId(userId: String) = 
        dailyGoalDao.deleteGoalByUserId(userId)

    fun calculateBMR(weight: Float, height: Float, age: Int, gender: Gender): Int {
        return when (gender) {
            Gender.MALE -> (10 * weight + 6.25f * height - 5 * age + 5).toInt()
            Gender.FEMALE -> (10 * weight + 6.25f * height - 5 * age - 161).toInt()
        }
    }

    fun calculateTDEE(bmr: Int, activityLevel: ActivityLevel): Int {
        val multiplier = when (activityLevel) {
            ActivityLevel.SEDENTARY -> 1.2f
            ActivityLevel.LIGHT -> 1.375f
            ActivityLevel.MODERATE -> 1.55f
            ActivityLevel.ACTIVE -> 1.725f
            ActivityLevel.VERY_ACTIVE -> 1.9f
        }
        return (bmr * multiplier).toInt()
    }
}
