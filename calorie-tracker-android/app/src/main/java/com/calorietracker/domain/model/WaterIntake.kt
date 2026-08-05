package com.calorietracker.domain.model

data class WaterIntake(
    val id: Int,
    val volumeMl: Int,
    val date: String, // ISO 8601
    val createdAt: Long
)
