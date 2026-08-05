package com.calorietracker.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SplashUiState(
    val isLoading: Boolean = true,
    val isLoggedIn: Boolean = false
)

sealed class SplashEvent {
    object NavigateToLogin : SplashEvent()
    object NavigateToDashboard : SplashEvent()
}

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    private val _events = MutableStateFlow<SplashEvent?>(null)
    val events: StateFlow<SplashEvent?> = _events.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            // Simulate checking auth token from DataStore/SharedPreferences
            delay(1500) // Show splash for 1.5 seconds
            
            // TODO: Replace with actual auth check from repository
            val isLoggedIn = false // This should come from auth repository
            
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isLoggedIn = isLoggedIn
            )
            
            _events.value = if (isLoggedIn) {
                SplashEvent.NavigateToDashboard
            } else {
                SplashEvent.NavigateToLogin
            }
        }
    }

    fun consumeEvent() {
        _events.value = null
    }
}
