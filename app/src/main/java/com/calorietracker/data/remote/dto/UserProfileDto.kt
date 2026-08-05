package com.calorietracker.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserProfileDto(
    @Json(name = "id") val id: Int,
    @Json(name = "email") val email: String,
    @Json(name = "username") val username: String,
    @Json(name = "gender") val gender: String?,
    @Json(name = "age") val age: Int?,
    @Json(name = "height_cm") val heightCm: Double?,
    @Json(name = "weight_kg") val weightKg: Double?,
    @Json(name = "activity_level") val activityLevel: String?,
    @Json(name = "daily_calorie_goal") val dailyCalorieGoal: Int?,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "updated_at") val updatedAt: String
)

@JsonClass(generateAdapter = true)
data class UpdateUserProfileRequestDto(
    @Json(name = "username") val username: String?,
    @Json(name = "gender") val gender: String?,
    @Json(name = "age") val age: Int?,
    @Json(name = "height_cm") val heightCm: Double?,
    @Json(name = "weight_kg") val weightKg: Double?,
    @Json(name = "activity_level") val activityLevel: String?,
    @Json(name = "daily_calorie_goal") val dailyCalorieGoal: Int?
)
