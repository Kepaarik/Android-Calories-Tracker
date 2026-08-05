package com.calorietracker.ui.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calorietracker.domain.model.DiaryEntry
import com.calorietracker.domain.usecase.diary.CalculateDailySummaryUseCase
import com.calorietracker.domain.usecase.diary.GetDiaryEntriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class DailyStats(
    val date: LocalDate,
    val calories: Double,
    val proteins: Double,
    val fats: Double,
    val carbs: Double
)

data class StatisticsUiState(
    val selectedPeriod: StatisticsPeriod = StatisticsPeriod.WEEK,
    val dailyStats: List<DailyStats> = emptyList(),
    val averageCalories: Double = 0.0,
    val averageProteins: Double = 0.0,
    val averageFats: Double = 0.0,
    val averageCarbs: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showCaloriesChart: Boolean = true,
    val showMacrosChart: Boolean = true
)

enum class StatisticsPeriod {
    DAY, WEEK, MONTH
}

sealed class StatisticsEvent {
    object ShowError : StatisticsEvent()
}

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getDiaryEntriesUseCase: GetDiaryEntriesUseCase,
    private val calculateDailySummaryUseCase: CalculateDailySummaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    private val _events = MutableStateFlow<StatisticsEvent?>(null)
    val events: StateFlow<StatisticsEvent?> = _events.asStateFlow()

    init {
        loadStatistics()
    }

    fun changePeriod(period: StatisticsPeriod) {
        if (period != _uiState.value.selectedPeriod) {
            _uiState.value = _uiState.value.copy(selectedPeriod = period)
            loadStatistics()
        }
    }

    fun toggleCaloriesChart() {
        _uiState.value = _uiState.value.copy(showCaloriesChart = !_uiState.value.showCaloriesChart)
    }

    fun toggleMacrosChart() {
        _uiState.value = _uiState.value.copy(showMacrosChart = !_uiState.value.showMacrosChart)
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val period = _uiState.value.selectedPeriod
            val endDate = LocalDate.now()
            val startDate = when (period) {
                StatisticsPeriod.DAY -> endDate
                StatisticsPeriod.WEEK -> endDate.minusDays(6)
                StatisticsPeriod.MONTH -> endDate.minusDays(29)
            }

            val dates = generateDates(startDate, endDate)
            val dailyStatsList = mutableListOf<DailyStats>()
            var totalCalories = 0.0
            var totalProteins = 0.0
            var totalFats = 0.0
            var totalCarbs = 0.0

            for (date in dates) {
                val summaryResult = calculateDailySummaryUseCase(date)
                val summary = summaryResult.getOrNull()
                
                if (summary != null) {
                    dailyStatsList.add(
                        DailyStats(
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

            val count = dailyStatsList.size.coerceAtLeast(1)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                dailyStats = dailyStatsList,
                averageCalories = totalCalories / count,
                averageProteins = totalProteins / count,
                averageFats = totalFats / count,
                averageCarbs = totalCarbs / count
            )
        }
    }

    private fun generateDates(start: LocalDate, end: LocalDate): List<LocalDate> {
        val dates = mutableListOf<LocalDate>()
        var current = start
        while (!current.isAfter(end)) {
            dates.add(current)
            current = current.plusDays(1)
        }
        return dates
    }

    fun consumeEvent() {
        _events.value = null
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
