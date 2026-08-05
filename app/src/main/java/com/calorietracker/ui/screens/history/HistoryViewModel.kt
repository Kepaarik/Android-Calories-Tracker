package com.calorietracker.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calorietracker.domain.model.DiaryEntry
import com.calorietracker.domain.usecase.diary.DeleteDiaryEntryUseCase
import com.calorietracker.domain.usecase.diary.GetDiaryEntriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class HistoryUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val diaryEntries: List<DiaryEntry> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showDeleteConfirmation: Boolean = false,
    val entryToDelete: DiaryEntry? = null
)

sealed class HistoryEvent {
    object ShowError : HistoryEvent()
    data class EntryDeleted(val entryId: Int) : HistoryEvent()
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getDiaryEntriesUseCase: GetDiaryEntriesUseCase,
    private val deleteDiaryEntryUseCase: DeleteDiaryEntryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private val _events = MutableStateFlow<HistoryEvent?>(null)
    val events: StateFlow<HistoryEvent?> = _events.asStateFlow()

    init {
        loadHistory()
    }

    fun selectDate(date: LocalDate) {
        if (date != _uiState.value.selectedDate) {
            _uiState.value = _uiState.value.copy(selectedDate = date)
            loadHistory()
        }
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val date = _uiState.value.selectedDate
            val result = getDiaryEntriesUseCase(date)
            
            result.fold(
                onSuccess = { entries ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        diaryEntries = entries.sortedByDescending { it.createdAt }
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
                    _events.value = HistoryEvent.ShowError
                }
            )
        }
    }

    fun confirmDelete(entry: DiaryEntry) {
        _uiState.value = _uiState.value.copy(
            showDeleteConfirmation = true,
            entryToDelete = entry
        )
    }

    fun dismissDeleteConfirmation() {
        _uiState.value = _uiState.value.copy(
            showDeleteConfirmation = false,
            entryToDelete = null
        )
    }

    fun deleteEntry() {
        val entry = _uiState.value.entryToDelete ?: return
        
        viewModelScope.launch {
            val result = deleteDiaryEntryUseCase(entry.id)
            
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        showDeleteConfirmation = false,
                        entryToDelete = null
                    )
                    loadHistory() // Reload the list
                    _events.value = HistoryEvent.EntryDeleted(entry.id)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        showDeleteConfirmation = false,
                        entryToDelete = null,
                        error = error.message
                    )
                    _events.value = HistoryEvent.ShowError
                }
            )
        }
    }

    fun consumeEvent() {
        _events.value = null
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
