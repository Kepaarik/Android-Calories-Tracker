package com.calorietracker.domain.model

data class DiaryEntry(
    val id: Int,
    val productId: Int,
    val product: Product,
    val mealType: MealType,
    val weightGrams: Int,
    val calories: Double,
    val proteins: Double,
    val fats: Double,
    val carbs: Double,
    val date: String, // ISO 8601
    val createdAt: Long
)
