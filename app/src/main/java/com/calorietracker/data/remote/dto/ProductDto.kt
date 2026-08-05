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
    @Json(name = "barcode") val barcode: String?,
    @Json(name = "created_at") val createdAt: Long
)

@JsonClass(generateAdapter = true)
data class CreateProductRequestDto(
    @Json(name = "name") val name: String,
    @Json(name = "calories_per_100g") val caloriesPer100g: Double,
    @Json(name = "proteins_per_100g") val proteinsPer100g: Double,
    @Json(name = "fats_per_100g") val fatsPer100g: Double,
    @Json(name = "carbs_per_100g") val carbsPer100g: Double,
    @Json(name = "barcode") val barcode: String?
)

@JsonClass(generateAdapter = true)
data class SearchProductsResponseDto(
    @Json(name = "products") val products: List<ProductDto>,
    @Json(name = "total") val total: Int
)
