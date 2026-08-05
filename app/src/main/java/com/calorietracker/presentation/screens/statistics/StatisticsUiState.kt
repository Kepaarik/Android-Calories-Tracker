package com.calorietracker.presentation.screens.statistics

import com.calorietracker.domain.model.WeightEntry
import java.time.LocalDate

data class StatisticsUiState(
    val isLoading: Boolean = false,
    val weeklyData: List<WeeklyDataPoint> = emptyList(),
    val weightHistory: List<WeightEntry> = emptyList(),
    val averageDailyCalories: Double = 0.0,
    val averageDailyProteins: Double = 0.0,
    val averageDailyFats: Double = 0.0,
    val averageDailyCarbs: Double = 0.0,
    val selectedTimeRange: TimeRange = TimeRange.WEEK,
    val error: String? = null
)

data class WeeklyDataPoint(
    val date: LocalDate,
    val calories: Double,
    val proteins: Double,
    val fats: Double,
    val carbs: Double
)

enum class TimeRange {
    WEEK, MONTH, YEAR
}
