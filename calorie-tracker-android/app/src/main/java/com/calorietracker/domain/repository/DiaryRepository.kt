package com.calorietracker.domain.repository

import com.calorietracker.domain.model.DiaryEntry
import java.time.LocalDate

interface DiaryRepository {
    suspend fun getEntriesByDate(date: LocalDate): List<DiaryEntry>
    suspend fun getEntriesByDateRange(startDate: LocalDate, endDate: LocalDate): List<DiaryEntry>
    suspend fun addEntry(entry: DiaryEntry): Result<DiaryEntry>
    suspend fun updateEntry(entry: DiaryEntry): Result<Unit>
    suspend fun deleteEntry(id: Int): Result<Unit>
}
