package com.calorietracker.data.repository

import com.calorietracker.data.local.FoodEntryDao
import com.calorietracker.data.model.FoodEntry
import com.calorietracker.data.model.MealType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodRepository @Inject constructor(
    private val foodEntryDao: FoodEntryDao
) {

    fun getAllEntries(userId: String): List<FoodEntry> = 
        foodEntryDao.getAllEntries(userId)

    fun getTodayEntries(userId: String): List<FoodEntry> = 
        foodEntryDao.getTodayEntries(userId)

    fun getEntriesByMealType(userId: String, mealType: MealType): List<FoodEntry> = 
        foodEntryDao.getEntriesByMealType(userId, mealType)

    fun getTodayCalories(userId: String): Int = 
        foodEntryDao.getTodayCalories(userId) ?: 0

    fun getTodayProtein(userId: String): Float = 
        foodEntryDao.getTodayProtein(userId) ?: 0f

    fun getTodayCarbs(userId: String): Float = 
        foodEntryDao.getTodayCarbs(userId) ?: 0f

    fun getTodayFats(userId: String): Float = 
        foodEntryDao.getTodayFats(userId) ?: 0f

    fun addEntry(entry: FoodEntry): Long = 
        foodEntryDao.insertEntry(entry)

    fun updateEntry(entry: FoodEntry) = 
        foodEntryDao.updateEntry(entry)

    fun deleteEntry(entry: FoodEntry) = 
        foodEntryDao.deleteEntry(entry)

    fun deleteEntryById(userId: String, id: Long) = 
        foodEntryDao.deleteEntryById(userId, id)

    fun clearAllUserEntries(userId: String) = 
        foodEntryDao.deleteAllUserEntries(userId)
}
