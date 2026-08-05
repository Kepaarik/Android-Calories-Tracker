package com.calorietracker.domain.model

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val age: Int? = null,
    val gender: Gender? = null,
    val heightCm: Double? = null,
    val weightKg: Double? = null,
    val activityLevel: ActivityLevel? = null,
    val createdAt: Long
)

enum class Gender {
    MALE, FEMALE
}

enum class ActivityLevel(val multiplier: Double) {
    SEDENTARY(1.2),
    LIGHTLY_ACTIVE(1.375),
    MODERATELY_ACTIVE(1.55),
    VERY_ACTIVE(1.725),
    EXTRA_ACTIVE(1.9)
}
