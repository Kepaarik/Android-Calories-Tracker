package com.calorietracker.domain.usecase.diary

import com.calorietracker.domain.model.DiaryEntry
import com.calorietracker.domain.repository.DiaryRepository
import java.time.LocalDate

data class DailySummary(
    val date: LocalDate,
    val totalCalories: Double = 0.0,
    val totalProteins: Double = 0.0,
    val totalFats: Double = 0.0,
    val totalCarbs: Double = 0.0,
    val entriesCount: Int = 0
)

class CalculateDailySummaryUseCase(
    private val diaryRepository: DiaryRepository
) {
    suspend operator fun invoke(date: LocalDate): DailySummary {
        val entries = diaryRepository.getEntriesByDate(date)
        
        return entries.fold(DailySummary(date = date)) { acc, entry ->
            acc.copy(
                totalCalories = acc.totalCalories + entry.calories,
                totalProteins = acc.totalProteins + entry.proteins,
                totalFats = acc.totalFats + entry.fats,
                totalCarbs = acc.totalCarbs + entry.carbs,
                entriesCount = acc.entriesCount + 1
            )
        }
    }
}
