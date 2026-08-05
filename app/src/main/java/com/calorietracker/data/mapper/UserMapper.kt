package com.calorietracker.data.mapper

import com.calorietracker.data.remote.dto.UserDto
import com.calorietracker.data.remote.dto.UserProfileDto
import com.calorietracker.domain.model.ActivityLevel
import com.calorietracker.domain.model.Gender
import com.calorietracker.domain.model.User
import com.calorietracker.domain.model.UserProfile

object UserMapper {

    fun UserDto.toDomain(): User {
        return User(
            id = this.id,
            email = this.email,
            username = this.username,
            createdAt = this.createdAt
        )
    }

    fun UserProfileDto.toDomain(): UserProfile {
        return UserProfile(
            id = this.id,
            email = this.email,
            username = this.username,
            gender = this.gender?.let { parseGender(it) },
            age = this.age,
            heightCm = this.heightCm,
            weightKg = this.weightKg,
            activityLevel = this.activityLevel?.let { parseActivityLevel(it) },
            dailyCalorieGoal = this.dailyCalorieGoal,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }

    private fun parseGender(gender: String): Gender {
        return when (gender.lowercase()) {
            "male" -> Gender.MALE
            "female" -> Gender.FEMALE
            else -> Gender.MALE
        }
    }

    private fun parseActivityLevel(level: String): ActivityLevel {
        return when (level.uppercase()) {
            "SEDENTARY" -> ActivityLevel.SEDENTARY
            "LIGHT" -> ActivityLevel.LIGHT
            "MODERATE" -> ActivityLevel.MODERATE
            "ACTIVE" -> ActivityLevel.ACTIVE
            "VERY_ACTIVE" -> ActivityLevel.VERY_ACTIVE
            else -> ActivityLevel.SEDENTARY
        }
    }

    fun Gender.toDto(): String {
        return when (this) {
            Gender.MALE -> "male"
            Gender.FEMALE -> "female"
        }
    }

    fun ActivityLevel.toDto(): String {
        return when (this) {
            ActivityLevel.SEDENTARY -> "sedentary"
            ActivityLevel.LIGHT -> "light"
            ActivityLevel.MODERATE -> "moderate"
            ActivityLevel.ACTIVE -> "active"
            ActivityLevel.VERY_ACTIVE -> "very_active"
        }
    }
}
