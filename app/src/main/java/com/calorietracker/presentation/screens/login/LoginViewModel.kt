package com.calorietracker.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calorietracker.domain.usecase.auth.LoginUseCase
import com.calorietracker.domain.usecase.auth.RegisterUseCase
import com.calorietracker.domain.usecase.auth.TelegramAuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val telegramAuthUseCase: TelegramAuthUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, error = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, error = null)
    }

    fun onUsernameChange(username: String) {
        _uiState.value = _uiState.value.copy(username = username, error = null)
    }

    fun login() {
        val state = _uiState.value
        if (!validateForm()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            loginUseCase(state.email, state.password)
                .onSuccess { authResult ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        error = null
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Login failed"
                    )
                }
        }
    }

    fun register() {
        val state = _uiState.value
        if (!validateForm(isRegistration = true)) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            registerUseCase(state.username!!, state.email, state.password)
                .onSuccess { authResult ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        error = null
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Registration failed"
                    )
                }
        }
    }

    fun telegramAuth(initData: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            telegramAuthUseCase(initData)
                .onSuccess { authResult ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        error = null
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Telegram auth failed"
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun validateForm(isRegistration: Boolean = false): Boolean {
        val state = _uiState.value
        
        if (state.email.isBlank() || !state.email.contains("@")) {
            _uiState.value = _uiState.value.copy(error = "Invalid email address")
            return false
        }
        
        if (state.password.length < 6) {
            _uiState.value = _uiState.value.copy(error = "Password must be at least 6 characters")
            return false
        }
        
        if (isRegistration && state.username.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(error = "Username is required")
            return false
        }
        
        return true
    }
}
