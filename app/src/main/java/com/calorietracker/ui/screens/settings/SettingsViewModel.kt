package com.calorietracker.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calorietracker.ui.theme.ThemeType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val themeType: ThemeType = ThemeType.SYSTEM,
    val notificationsEnabled: Boolean = true,
    val waterReminderEnabled: Boolean = true,
    val mealRemindersEnabled: Boolean = false,
    val unitSystem: UnitSystem = UnitSystem.METRIC,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showSuccessMessage: Boolean = false
)

enum class UnitSystem {
    METRIC, IMPERIAL
}

sealed class SettingsEvent {
    object ShowError : SettingsEvent()
    object Logout : SettingsEvent()
    object NavigateToLogin : SettingsEvent()
}

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _events = MutableStateFlow<SettingsEvent?>(null)
    val events: StateFlow<SettingsEvent?> = _events.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            // TODO: Load settings from DataStore
            // For now using defaults
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                themeType = ThemeType.SYSTEM,
                notificationsEnabled = true,
                waterReminderEnabled = true,
                mealRemindersEnabled = false,
                unitSystem = UnitSystem.METRIC
            )
        }
    }

    fun onThemeTypeChange(themeType: ThemeType) {
        _uiState.value = _uiState.value.copy(themeType = themeType)
        saveSettings()
    }

    fun toggleNotifications() {
        _uiState.value = _uiState.value.copy(notificationsEnabled = !_uiState.value.notificationsEnabled)
        saveSettings()
    }

    fun toggleWaterReminders() {
        _uiState.value = _uiState.value.copy(waterReminderEnabled = !_uiState.value.waterReminderEnabled)
        saveSettings()
    }

    fun toggleMealReminders() {
        _uiState.value = _uiState.value.copy(mealRemindersEnabled = !_uiState.value.mealRemindersEnabled)
        saveSettings()
    }

    fun onUnitSystemChange(unitSystem: UnitSystem) {
        _uiState.value = _uiState.value.copy(unitSystem = unitSystem)
        saveSettings()
    }

    private fun saveSettings() {
        viewModelScope.launch {
            // TODO: Save settings to DataStore
            _uiState.value = _uiState.value.copy(showSuccessMessage = true)
            
            // Auto-hide success message after delay
            kotlinx.coroutines.delay(2000)
            _uiState.value = _uiState.value.copy(showSuccessMessage = false)
        }
    }

    fun logout() {
        _events.value = SettingsEvent.Logout
    }

    fun consumeEvent() {
        _events.value = null
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun dismissSuccessMessage() {
        _uiState.value = _uiState.value.copy(showSuccessMessage = false)
    }
}
