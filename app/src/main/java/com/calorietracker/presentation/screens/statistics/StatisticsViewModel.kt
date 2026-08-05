package com.calorietracker.presentation.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calorietracker.domain.usecase.diary.CalculateDailySummaryUseCase
import com.calorietracker.domain.usecase.weight.GetWeightHistoryByDateRangeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val calculateDailySummaryUseCase: CalculateDailySummaryUseCase,
    private val getWeightHistoryByDateRangeUseCase: GetWeightHistoryByDateRangeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadWeeklyData()
    }

    fun loadWeeklyData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val today = LocalDate.now()
            val startDate = today.minusDays(6) // 7 days including today
            
            // Load daily summaries for the week
            val dailySummaries = mutableListOf<WeeklyDataPoint>()
            var totalCalories = 0.0
            var totalProteins = 0.0
            var totalFats = 0.0
            var totalCarbs = 0.0
            
            for (i in 0..6) {
                val date = startDate.plusDays(i.toLong())
                calculateDailySummaryUseCase(date)
                    .onSuccess { summary ->
                        dailySummaries.add(
                            WeeklyDataPoint(
                                date = date,
                                calories = summary.totalCalories,
                                proteins = summary.totalProteins,
                                fats = summary.totalFats,
                                carbs = summary.totalCarbs
                            )
                        )
                        totalCalories += summary.totalCalories
                        totalProteins += summary.totalProteins
                        totalFats += summary.totalFats
                        totalCarbs += summary.totalCarbs
                    }
            }
            
            // Load weight history
            val weightEntries = getWeightHistoryByDateRangeUseCase(startDate, today)
                .getOrElse { emptyList() }
            
            val averageCalories = if (dailySummaries.isNotEmpty()) totalCalories / dailySummaries.size else 0.0
            
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                weeklyData = dailySummaries,
                weightHistory = weightEntries,
                averageDailyCalories = averageCalories,
                averageDailyProteins = totalProteins / 7,
                averageDailyFats = totalFats / 7,
                averageDailyCarbs = totalCarbs / 7,
                error = null
            )
        }
    }

    fun setTimeRange(range: TimeRange) {
        _uiState.value = _uiState.value.copy(selectedTimeRange = range)
        when (range) {
            TimeRange.WEEK -> loadWeeklyData()
            TimeRange.MONTH -> loadMonthlyData()
            TimeRange.YEAR -> loadYearlyData()
        }
    }

    private fun loadMonthlyData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            // TODO: Implement monthly data loading
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    private fun loadYearlyData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            // TODO: Implement yearly data loading
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
