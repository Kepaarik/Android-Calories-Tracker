package com.calorietracker.presentation.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calorietracker.domain.model.DiaryEntry
import com.calorietracker.domain.model.MealType
import com.calorietracker.domain.usecase.diary.*
import com.calorietracker.domain.usecase.weight.GetWeightHistoryUseCase
import com.calorietracker.domain.usecase.weight.AddWeightEntryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDiaryEntriesUseCase: GetDiaryEntriesUseCase,
    private val addDiaryEntryUseCase: AddDiaryEntryUseCase,
    private val deleteDiaryEntryUseCase: DeleteDiaryEntryUseCase,
    private val calculateDailySummaryUseCase: CalculateDailySummaryUseCase,
    private val getWeightHistoryUseCase: GetWeightHistoryUseCase,
    private val addWeightEntryUseCase: AddWeightEntryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private var currentDate = LocalDate.now()

    init {
        loadDiaryEntries()
        loadDailySummary()
        loadLatestWeight()
    }

    fun loadDiaryEntries() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            getDiaryEntriesUseCase(currentDate)
                .onSuccess { entries ->
                    val groupedEntries = entries.groupBy { it.mealType }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        diaryEntries = groupedEntries,
                        error = null
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to load diary entries"
                    )
                }
        }
    }

    fun loadDailySummary() {
        viewModelScope.launch {
            calculateDailySummaryUseCase(currentDate)
                .onSuccess { summary ->
                    _uiState.value = _uiState.value.copy(dailySummary = summary)
                }
                .onFailure { exception ->
                    // Handle error silently or show notification
                }
        }
    }

    fun loadLatestWeight() {
        viewModelScope.launch {
            getWeightHistoryUseCase(limit = 1)
                .onSuccess { weights ->
                    val latestWeight = weights.firstOrNull()
                    _uiState.value = _uiState.value.copy(latestWeight = latestWeight)
                }
                .onFailure { exception ->
                    // Handle error silently
                }
        }
    }

    fun addDiaryEntry(entry: DiaryEntry) {
        viewModelScope.launch {
            addDiaryEntryUseCase(entry)
                .onSuccess {
                    loadDiaryEntries()
                    loadDailySummary()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Failed to add entry"
                    )
                }
        }
    }

    fun deleteDiaryEntry(entryId: Int) {
        viewModelScope.launch {
            deleteDiaryEntryUseCase(entryId)
                .onSuccess {
                    loadDiaryEntries()
                    loadDailySummary()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Failed to delete entry"
                    )
                }
        }
    }

    fun updateDate(date: LocalDate) {
        currentDate = date
        loadDiaryEntries()
        loadDailySummary()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
