package com.calorietracker.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.calorietracker.data.local.CalorieTrackerDatabase
import com.calorietracker.data.local.entity.FoodEntryEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class FoodEntryDaoTest {

    private lateinit var database: CalorieTrackerDatabase
    private lateinit var foodEntryDao: FoodEntryDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, CalorieTrackerDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        foodEntryDao = database.foodEntryDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndRetrieveFoodEntry() = runTest {
        // Given
        val entry = FoodEntryEntity(
            id = 1,
            productId = 1,
            productName = "Apple",
            mealType = "BREAKFAST",
            weightGrams = 150,
            calories = 78.0,
            proteins = 0.45f,
            fats = 0.3f,
            carbs = 21.0f,
            date = LocalDate.now().toString(),
            createdAt = System.currentTimeMillis()
        )

        // When
        foodEntryDao.insert(entry)
        val retrievedEntries = foodEntryDao.getEntriesByDate(LocalDate.now().toString()).first()

        // Then
        assertEquals(1, retrievedEntries.size)
        assertEquals("Apple", retrievedEntries.first().productName)
        assertEquals(78.0, retrievedEntries.first().calories, 0.01)
    }

    @Test
    fun deleteFoodEntry() = runTest {
        // Given
        val entry = FoodEntryEntity(
            id = 1,
            productId = 1,
            productName = "Apple",
            mealType = "BREAKFAST",
            weightGrams = 150,
            calories = 78.0,
            proteins = 0.45f,
            fats = 0.3f,
            carbs = 21.0f,
            date = LocalDate.now().toString(),
            createdAt = System.currentTimeMillis()
        )
        foodEntryDao.insert(entry)

        // When
        foodEntryDao.delete(entry)
        val retrievedEntries = foodEntryDao.getEntriesByDate(LocalDate.now().toString()).first()

        // Then
        assertTrue(retrievedEntries.isEmpty())
    }

    @Test
    fun getEntriesByMealType() = runTest {
        // Given
        val breakfastEntry = FoodEntryEntity(
            id = 1,
            productId = 1,
            productName = "Oatmeal",
            mealType = "BREAKFAST",
            weightGrams = 200,
            calories = 150.0,
            proteins = 5.0f,
            fats = 3.0f,
            carbs = 27.0f,
            date = LocalDate.now().toString(),
            createdAt = System.currentTimeMillis()
        )
        val lunchEntry = FoodEntryEntity(
            id = 2,
            productId = 2,
            productName = "Chicken",
            mealType = "LUNCH",
            weightGrams = 250,
            calories = 400.0,
            proteins = 40.0f,
            fats = 10.0f,
            carbs = 0.0f,
            date = LocalDate.now().toString(),
            createdAt = System.currentTimeMillis()
        )
        
        foodEntryDao.insert(breakfastEntry)
        foodEntryDao.insert(lunchEntry)

        // When
        val breakfastEntries = foodEntryDao.getEntriesByDateAndMealType(
            LocalDate.now().toString(),
            "BREAKFAST"
        ).first()

        // Then
        assertEquals(1, breakfastEntries.size)
        assertEquals("Oatmeal", breakfastEntries.first().productName)
    }
}
