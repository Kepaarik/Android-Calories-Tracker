package com.calorietracker.data.model

data class BarcodeProduct(
    val barcode: String,
    val name: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fats: Float,
    val servingSize: Float = 100f,
    val servingUnit: String = "г",
    val brand: String? = null,
    val category: String? = null
)
