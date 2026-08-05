package com.calorietracker.presentation.screens.weighthistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calorietracker.domain.usecase.weight.AddWeightEntryUseCase
import com.calorietracker.domain.usecase.weight.DeleteWeightEntryUseCase
import com.calorietracker.domain.usecase.weight.GetWeightHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class WeightHistoryViewModel @Inject constructor(
    private val getWeightHistoryUseCase: GetWeightHistoryUseCase,
    private val addWeightEntryUseCase: AddWeightEntryUseCase,
    private val deleteWeightEntryUseCase: DeleteWeightEntryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeightHistoryUiState())
    val uiState: StateFlow<WeightHistoryUiState> = _uiState.asStateFlow()

    init {
        loadWeightHistory()
    }

    fun loadWeightHistory() {
        viewModelScope.launch {
            _uiState.value = WeightHistoryUiState(isLoading = true)
            
            getWeightHistoryUseCase()
                .onSuccess { entries ->
                    _uiState.value = WeightHistoryUiState(
                        isLoading = false,
                        weightEntries = entries,
                        error = null
                    )
                }
                .onFailure { exception ->
                    _uiState.value = WeightHistoryUiState(
                        isLoading = false,
                        error = exception.message ?: "Ошибка загрузки истории веса"
                    )
                }
        }
    }

    fun addWeightEntry(weightKg: Double, date: LocalDate = LocalDate.now()) {
        viewModelScope.launch {
            addWeightEntryUseCase(weightKg, date)
                .onSuccess {
                    loadWeightHistory() // Перезагрузить список
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Ошибка добавления записи"
                    )
                }
        }
    }

    fun deleteWeightEntry(entryId: Int) {
        viewModelScope.launch {
            deleteWeightEntryUseCase(entryId)
                .onSuccess {
                    loadWeightHistory() // Перезагрузить список
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Ошибка удаления записи"
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class WeightHistoryUiState(
    val isLoading: Boolean = false,
    val weightEntries: List<com.calorietracker.domain.model.WeightEntry> = emptyList(),
    val error: String? = null
)
