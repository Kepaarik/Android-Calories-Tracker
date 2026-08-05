package com.calorietracker.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequestDto(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class RegisterRequestDto(
    @Json(name = "username") val username: String,
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class TelegramAuthRequestDto(
    @Json(name = "init_data") val initData: String
)

@JsonClass(generateAdapter = true)
data class UserResponseDto(
    @Json(name = "id") val id: Int,
    @Json(name = "username") val username: String,
    @Json(name = "email") val email: String,
    @Json(name = "age") val age: Int? = null,
    @Json(name = "gender") val gender: String? = null,
    @Json(name = "height_cm") val heightCm: Double? = null,
    @Json(name = "weight_kg") val weightKg: Double? = null,
    @Json(name = "activity_level") val activityLevel: String? = null,
    @Json(name = "created_at") val createdAt: Long
)
