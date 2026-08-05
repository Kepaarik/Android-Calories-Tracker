package com.calorietracker.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "calories_per_100g") val caloriesPer100g: Double,
    @Json(name = "proteins_per_100g") val proteinsPer100g: Double,
    @Json(name = "fats_per_100g") val fatsPer100g: Double,
    @Json(name = "carbs_per_100g") val carbsPer100g: Double,
    @Json(name = "barcode") val barcode: String? = null,
    @Json(name = "created_at") val createdAt: Long
)

@JsonClass(generateAdapter = true)
data class DiaryEntryDto(
    @Json(name = "id") val id: Int,
    @Json(name = "product_id") val productId: Int,
    @Json(name = "meal_type") val mealType: String,
    @Json(name = "weight_grams") val weightGrams: Int,
    @Json(name = "calories") val calories: Double,
    @Json(name = "proteins") val proteins: Double,
    @Json(name = "fats") val fats: Double,
    @Json(name = "carbs") val carbs: Double,
    @Json(name = "date") val date: String,
    @Json(name = "created_at") val createdAt: Long,
    @Json(name = "product") val product: ProductDto? = null
)
