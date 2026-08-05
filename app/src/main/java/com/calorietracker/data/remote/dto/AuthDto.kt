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
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
    @Json(name = "username") val username: String
)

@JsonClass(generateAdapter = true)
data class TelegramAuthRequestDto(
    @Json(name = "id") val id: Long,
    @Json(name = "first_name") val firstName: String?,
    @Json(name = "last_name") val lastName?,
    @Json(name = "username") val username: String?,
    @Json(name = "photo_url") val photoUrl: String?,
    @Json(name = "auth_date") val authDate: Long,
    @Json(name = "hash") val hash: String
)

@JsonClass(generateAdapter = true)
data class AuthResponseDto(
    @Json(name = "token") val token: String,
    @Json(name = "user") val user: UserDto
)

@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "id") val id: Int,
    @Json(name = "email") val email: String,
    @Json(name = "username") val username: String,
    @Json(name = "created_at") val createdAt: String
)
