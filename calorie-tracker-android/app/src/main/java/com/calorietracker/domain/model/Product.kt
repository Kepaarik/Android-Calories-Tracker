package com.calorietracker.domain.model

data class Product(
    val id: Int,
    val name: String,
    val caloriesPer100g: Double,
    val proteinsPer100g: Double,
    val fatsPer100g: Double,
    val carbsPer100g: Double,
    val barcode: String? = null,
    val createdAt: Long
)
