package com.calorietracker.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeightEntryDto(
    @Json(name = "id") val id: Int,
    @Json(name = "user_id") val userId: Int,
    @Json(name = "weight_kg") val weightKg: Double,
    @Json(name = "date") val date: String,
    @Json(name = "created_at") val createdAt: Long
)

@JsonClass(generateAdapter = true)
data class CreateWeightEntryRequestDto(
    @Json(name = "weight_kg") val weightKg: Double,
    @Json(name = "date") val date: String
)
