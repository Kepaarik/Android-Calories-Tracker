package com.calorietracker.data.repository

import com.calorietracker.data.local.FoodEntryDao
import com.calorietracker.data.model.FoodEntry
import com.calorietracker.data.model.MealType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodRepository @Inject constructor(
    private val foodEntryDao: FoodEntryDao
) {

    fun getAllEntries(userId: String): Flow<List<FoodEntry>> = 
        foodEntryDao.getAllEntries(userId)

    fun getTodayEntries(userId: String): Flow<List<FoodEntry>> = 
        foodEntryDao.getTodayEntries(userId)

    fun getEntriesByMealType(userId: String, mealType: MealType): Flow<List<FoodEntry>> = 
        foodEntryDao.getEntriesByMealType(userId, mealType)

    suspend fun getTodayCalories(userId: String): Int = 
        foodEntryDao.getTodayCalories(userId) ?: 0

    suspend fun getTodayProtein(userId: String): Float = 
        foodEntryDao.getTodayProtein(userId) ?: 0f

    suspend fun getTodayCarbs(userId: String): Float = 
        foodEntryDao.getTodayCarbs(userId) ?: 0f

    suspend fun getTodayFats(userId: String): Float = 
        foodEntryDao.getTodayFats(userId) ?: 0f

    suspend fun addEntry(entry: FoodEntry): Long = 
        foodEntryDao.insertEntry(entry)

    suspend fun updateEntry(entry: FoodEntry) = 
        foodEntryDao.updateEntry(entry)

    suspend fun deleteEntry(entry: FoodEntry) = 
        foodEntryDao.deleteEntry(entry)

    suspend fun deleteEntryById(userId: String, id: Long) = 
        foodEntryDao.deleteEntryById(userId, id)

    suspend fun clearAllUserEntries(userId: String) = 
        foodEntryDao.deleteAllUserEntries(userId)
}
