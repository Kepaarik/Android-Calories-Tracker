package com.calorietracker.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calorietracker.domain.model.DiaryEntry
import com.calorietracker.domain.model.MealType
import com.calorietracker.domain.usecase.diary.AddDiaryEntryUseCase
import com.calorietracker.domain.usecase.diary.CalculateDailySummaryUseCase
import com.calorietracker.domain.usecase.diary.DeleteDiaryEntryUseCase
import com.calorietracker.domain.usecase.diary.GetDiaryEntriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class DailySummary(
    val totalCalories: Double = 0.0,
    val totalProteins: Double = 0.0,
    val totalFats: Double = 0.0,
    val totalCarbs: Double = 0.0,
    val calorieGoal: Int = 2000,
    val proteinGoal: Int = 150,
    val fatGoal: Int = 65,
    val carbGoal: Int = 250
)

data class DashboardUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val diaryEntries: List<DiaryEntry> = emptyList(),
    val dailySummary: DailySummary = DailySummary(),
    val waterIntakeMl: Int = 0,
    val waterGoalMl: Int = 2000,
    val currentWeight: Double? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val deletedEntry: DiaryEntry? = null
)

sealed class DashboardEvent {
    data class ShowUndoSnackbar(val entry: DiaryEntry) : DashboardEvent()
    object ShowError : DashboardEvent()
    object RefreshData : DashboardEvent()
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDiaryEntriesUseCase: GetDiaryEntriesUseCase,
    private val addDiaryEntryUseCase: AddDiaryEntryUseCase,
    private val deleteDiaryEntryUseCase: DeleteDiaryEntryUseCase,
    private val calculateDailySummaryUseCase: CalculateDailySummaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _events = MutableStateFlow<DashboardEvent?>(null)
    val events: StateFlow<DashboardEvent?> = _events.asStateFlow()

    init {
        loadDiaryEntries()
    }

    fun loadDiaryEntries() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val date = _uiState.value.selectedDate
            val result = getDiaryEntriesUseCase(date)
            
            result.fold(
                onSuccess = { entries ->
                    val summary = calculateDailySummaryUseCase(date).getOrNull() ?: DailySummary()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        diaryEntries = entries,
                        dailySummary = summary
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
                    _events.value = DashboardEvent.ShowError
                }
            )
        }
    }

    fun selectDate(date: LocalDate) {
        if (date != _uiState.value.selectedDate) {
            _uiState.value = _uiState.value.copy(selectedDate = date)
            loadDiaryEntries()
        }
    }

    fun deleteEntry(entry: DiaryEntry) {
        viewModelScope.launch {
            val previousEntries = _uiState.value.diaryEntries
            val newEntries = previousEntries.filter { it.id != entry.id }
            _uiState.value = _uiState.value.copy(
                diaryEntries = newEntries,
                deletedEntry = entry
            )
            
            val result = deleteDiaryEntryUseCase(entry.id)
            if (result.isFailure) {
                // Rollback on error
                _uiState.value = _uiState.value.copy(diaryEntries = previousEntries, deletedEntry = null)
                _events.value = DashboardEvent.ShowError
                return@launch
            }
            
            _events.value = DashboardEvent.ShowUndoSnackbar(entry)
            
            // Auto-clear deleted entry after undo period
            kotlinx.coroutines.delay(5000)
            _uiState.value = _uiState.value.copy(deletedEntry = null)
        }
    }

    fun undoDelete() {
        val deletedEntry = _uiState.value.deletedEntry ?: return
        
        viewModelScope.launch {
            val result = addDiaryEntryUseCase(
                productId = deletedEntry.product.id,
                weightGrams = deletedEntry.weightGrams,
                mealType = deletedEntry.mealType,
                date = _uiState.value.selectedDate
            )
            
            if (result.isSuccess) {
                loadDiaryEntries()
            }
            _uiState.value = _uiState.value.copy(deletedEntry = null)
        }
    }

    fun addWater(amountMl: Int) {
        val currentWater = _uiState.value.waterIntakeMl
        _uiState.value = _uiState.value.copy(
            waterIntakeMl = (currentWater + amountMl).coerceAtLeast(0)
        )
    }

    fun updateWeight(weight: Double) {
        _uiState.value = _uiState.value.copy(currentWeight = weight)
    }

    fun consumeEvent() {
        _events.value = null
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
