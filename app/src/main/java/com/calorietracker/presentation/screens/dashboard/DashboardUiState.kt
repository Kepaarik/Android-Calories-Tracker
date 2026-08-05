package com.calorietracker.presentation.screens.dashboard

import com.calorietracker.domain.model.DailySummary
import com.calorietracker.domain.model.DiaryEntry
import com.calorietracker.domain.model.MealType
import com.calorietracker.domain.model.WeightEntry

data class DashboardUiState(
    val isLoading: Boolean = false,
    val diaryEntries: Map<MealType, List<DiaryEntry>> = emptyMap(),
    val dailySummary: DailySummary? = null,
    val latestWeight: WeightEntry? = null,
    val waterIntakeMl: Int = 0,
    val targetWaterMl: Int = 2000,
    val error: String? = null
) {
    val breakfastEntries: List<DiaryEntry> get() = diaryEntries[MealType.BREAKFAST] ?: emptyList()
    val lunchEntries: List<DiaryEntry> get() = diaryEntries[MealType.LUNCH] ?: emptyList()
    val dinnerEntries: List<DiaryEntry> get() = diaryEntries[MealType.DINNER] ?: emptyList()
    val snackEntries: List<DiaryEntry> get() = diaryEntries[MealType.SNACK] ?: emptyList()
}
