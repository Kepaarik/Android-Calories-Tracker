package com.calorietracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_entries")
data class FoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val calories: Int,
    val protein: Float = 0f,
    val carbs: Float = 0f,
    val fats: Float = 0f,
    val servingSize: Float = 100f,
    val servingUnit: String = "г",
    val barcode: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val mealType: MealType = MealType.OTHER,
    val userId: String? = null
)

enum class MealType {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK,
    OTHER
}
