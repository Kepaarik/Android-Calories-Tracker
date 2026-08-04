package com.calorietracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_goals")
data class DailyGoal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val caloriesGoal: Int = 2000,
    val proteinGoal: Float = 150f,
    val carbsGoal: Float = 250f,
    val fatsGoal: Float = 70f,
    val waterGoal: Int = 8, // glasses
    val weight: Float? = null,
    val height: Float? = null,
    val age: Int? = null,
    val gender: Gender = Gender.MALE,
    val activityLevel: ActivityLevel = ActivityLevel.MODERATE,
    val lastUpdated: Long = System.currentTimeMillis()
)

enum class Gender {
    MALE,
    FEMALE
}

enum class ActivityLevel {
    SEDENTARY,
    LIGHT,
    MODERATE,
    ACTIVE,
    VERY_ACTIVE
}
