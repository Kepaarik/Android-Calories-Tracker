package com.calorietracker.domain.repository

import com.calorietracker.domain.model.WeightEntry
import java.time.LocalDate

interface WeightRepository {
    suspend fun getWeightHistory(userId: Int): List<WeightEntry>
    suspend fun getWeightHistoryByDateRange(userId: Int, startDate: LocalDate, endDate: LocalDate): List<WeightEntry>
    suspend fun addWeightEntry(entry: WeightEntry): Result<WeightEntry>
    suspend fun deleteWeightEntry(id: Int): Result<Unit>
}
