package com.calorietracker.domain.repository

import com.calorietracker.domain.model.WeightEntry
import java.time.LocalDate

interface WeightRepository {
    suspend fun getWeightHistory(fromDate: LocalDate? = null, toDate: LocalDate? = null): Result<List<WeightEntry>>
    suspend fun addWeightEntry(weightKg: Double, date: LocalDate): Result<WeightEntry>
    suspend fun deleteWeightEntry(id: Int): Result<Unit>
}
