package com.calorietracker.domain.model

data class User(
    val id: Int,
    val email: String,
    val username: String,
    val createdAt: String
)

data class UserProfile(
    val id: Int,
    val email: String,
    val username: String,
    val gender: Gender?,
    val age: Int?,
    val heightCm: Double?,
    val weightKg: Double?,
    val activityLevel: ActivityLevel?,
    val dailyCalorieGoal: Int?,
    val createdAt: String,
    val updatedAt: String
)

enum class Gender {
    MALE, FEMALE
}

enum class ActivityLevel(val multiplier: Double) {
    SEDENTARY(1.2),           // Минимальная активность
    LIGHT(1.375),             // Легкая активность 1-3 раза в неделю
    MODERATE(1.55),           // Средняя активность 3-5 раз в неделю
    ACTIVE(1.725),            // Высокая активность 6-7 раз в неделю
    VERY_ACTIVE(1.9)          // Очень высокая активность (физическая работа)
}
