package com.calorietracker.domain.model

data class WeightEntry(
    val id: Int,
    val weightKg: Double,
    val date: String, // ISO 8601
    val createdAt: Long
)
