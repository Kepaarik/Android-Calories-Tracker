package com.calorietracker.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DiaryEntryDto(
    @Json(name = "id") val id: Int,
    @Json(name = "user_id") val userId: Int,
    @Json(name = "product_id") val productId: Int,
    @Json(name = "meal_type") val mealType: String,
    @Json(name = "weight_grams") val weightGrams: Int,
    @Json(name = "calories") val calories: Double,
    @Json(name = "proteins") val proteins: Double,
    @Json(name = "fats") val fats: Double,
    @Json(name = "carbs") val carbs: Double,
    @Json(name = "date") val date: String,
    @Json(name = "created_at") val createdAt: Long
)

@JsonClass(generateAdapter = true)
data class CreateDiaryEntryRequestDto(
    @Json(name = "product_id") val productId: Int,
    @Json(name = "meal_type") val mealType: String,
    @Json(name = "weight_grams") val weightGrams: Int,
    @Json(name = "date") val date: String
)

@JsonClass(generateAdapter = true)
data class UpdateDiaryEntryRequestDto(
    @Json(name = "weight_grams") val weightGrams: Int?,
    @Json(name = "meal_type") val mealType: String?
)
