package com.calorietracker.domain.repository

import com.calorietracker.domain.model.DiaryEntry
import com.calorietracker.domain.model.DailySummary
import com.calorietracker.domain.model.MealType
import java.time.LocalDate

interface DiaryRepository {
    suspend fun getEntriesByDate(date: LocalDate): Result<List<DiaryEntry>>
    suspend fun getEntriesByMealType(date: LocalDate, mealType: MealType): Result<List<DiaryEntry>>
    suspend fun addEntry(entry: DiaryEntry): Result<DiaryEntry>
    suspend fun updateEntry(entry: DiaryEntry): Result<DiaryEntry>
    suspend fun deleteEntry(id: Int): Result<Unit>
    suspend fun getDailySummary(date: LocalDate): Result<DailySummary>
}
