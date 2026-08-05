package com.calorietracker.ui.screens.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calorietracker.data.model.ActivityRecord
import com.calorietracker.domain.usecase.weight.AddWeightEntryUseCase
import com.calorietracker.domain.usecase.weight.GetWeightHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class ActivityUiState(
    val activityRecords: List<ActivityRecord> = emptyList(),
    val weightHistory: List<Pair<LocalDate, Double>> = emptyList(),
    val currentWeight: Double? = null,
    val newWeightValue: Double = 70.0,
    val newWeightDate: LocalDate = LocalDate.now(),
    val isLoading: Boolean = false,
    val isSavingWeight: Boolean = false,
    val error: String? = null,
    val showAddWeightDialog: Boolean = false,
    val showSuccessMessage: Boolean = false
)

sealed class ActivityEvent {
    object ShowError : ActivityEvent()
    object WeightAdded : ActivityEvent()
}

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val getWeightHistoryUseCase: GetWeightHistoryUseCase,
    private val addWeightEntryUseCase: AddWeightEntryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActivityUiState())
    val uiState: StateFlow<ActivityUiState> = _uiState.asStateFlow()

    private val _events = MutableStateFlow<ActivityEvent?>(null)
    val events: StateFlow<ActivityEvent?> = _events.asStateFlow()

    init {
        loadWeightHistory()
    }

    fun loadWeightHistory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = getWeightHistoryUseCase()
            
            result.fold(
                onSuccess = { history ->
                    val weightHistory = history.map { it.date to it.weightKg }.sortedByDescending { it.first }
                    val currentWeight = history.maxByOrNull { it.date }?.weightKg
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        weightHistory = weightHistory,
                        currentWeight = currentWeight
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
                    _events.value = ActivityEvent.ShowError
                }
            )
        }
    }

    fun onNewWeightChange(weight: Double) {
        if (weight in 20.0..300.0) {
            _uiState.value = _uiState.value.copy(newWeightValue = weight)
        }
    }

    fun onNewWeightDateChange(date: LocalDate) {
        _uiState.value = _uiState.value.copy(newWeightDate = date)
    }

    fun showAddWeightDialog() {
        _uiState.value = _uiState.value.copy(showAddWeightDialog = true)
    }

    fun dismissAddWeightDialog() {
        _uiState.value = _uiState.value.copy(showAddWeightDialog = false)
    }

    fun addWeightEntry() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSavingWeight = true, error = null)
            
            val result = addWeightEntryUseCase(
                weightKg = _uiState.value.newWeightValue,
                date = _uiState.value.newWeightDate
            )
            
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isSavingWeight = false,
                        showAddWeightDialog = false,
                        showSuccessMessage = true
                    )
                    loadWeightHistory()
                    _events.value = ActivityEvent.WeightAdded
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isSavingWeight = false,
                        error = error.message
                    )
                    _events.value = ActivityEvent.ShowError
                }
            )
        }
    }

    fun dismissSuccessMessage() {
        _uiState.value = _uiState.value.copy(showSuccessMessage = false)
    }

    fun consumeEvent() {
        _events.value = null
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
