package com.calorietracker.data.local

import androidx.room.*
import com.calorietracker.data.model.FoodEntry
import com.calorietracker.data.model.MealType
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodEntryDao {

    @Query("SELECT * FROM food_entries WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllEntries(userId: String): Flow<List<FoodEntry>>

    @Query("SELECT * FROM food_entries WHERE userId = :userId AND date(timestamp / 1000, 'unixepoch') = date('now') ORDER BY timestamp DESC")
    fun getTodayEntries(userId: String): Flow<List<FoodEntry>>

    @Query("SELECT * FROM food_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): FoodEntry?

    @Query("SELECT SUM(calories) FROM food_entries WHERE userId = :userId AND date(timestamp / 1000, 'unixepoch') = date('now')")
    suspend fun getTodayCalories(userId: String): Int?

    @Query("SELECT SUM(protein) FROM food_entries WHERE userId = :userId AND date(timestamp / 1000, 'unixepoch') = date('now')")
    suspend fun getTodayProtein(userId: String): Float?

    @Query("SELECT SUM(carbs) FROM food_entries WHERE userId = :userId AND date(timestamp / 1000, 'unixepoch') = date('now')")
    suspend fun getTodayCarbs(userId: String): Float?

    @Query("SELECT SUM(fats) FROM food_entries WHERE userId = :userId AND date(timestamp / 1000, 'unixepoch') = date('now')")
    suspend fun getTodayFats(userId: String): Float?

    @Query("SELECT * FROM food_entries WHERE userId = :userId AND mealType = :mealType AND date(timestamp / 1000, 'unixepoch') = date('now')")
    fun getEntriesByMealType(userId: String, mealType: MealType): Flow<List<FoodEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: FoodEntry): Long

    @Update
    suspend fun updateEntry(entry: FoodEntry)

    @Delete
    suspend fun deleteEntry(entry: FoodEntry)

    @Query("DELETE FROM food_entries WHERE userId = :userId AND id = :id")
    suspend fun deleteEntryById(userId: String, id: Long)

    @Query("DELETE FROM food_entries WHERE userId = :userId")
    suspend fun deleteAllUserEntries(userId: String)
}
