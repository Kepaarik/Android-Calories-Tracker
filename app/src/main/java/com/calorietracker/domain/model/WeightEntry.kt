package com.calorietracker.domain.model

import java.time.LocalDate

data class WeightEntry(
    val id: Int,
    val userId: Int,
    val weightKg: Double,
    val date: LocalDate,
    val createdAt: Long
)
