package com.calorietracker.ui.screens.login

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

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
    val isRegistrationMode: Boolean = false
)

sealed class LoginEvent {
    object NavigateToDashboard : LoginEvent()
    object ShowError : LoginEvent()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val telegramAuthUseCase: TelegramAuthUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = MutableStateFlow<LoginEvent?>(null)
    val events: StateFlow<LoginEvent?> = _events.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, error = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, error = null)
    }

    fun onConfirmPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = password, error = null)
    }

    fun toggleRegistrationMode() {
        _uiState.value = _uiState.value.copy(
            isRegistrationMode = !_uiState.value.isRegistrationMode,
            error = null
        )
    }

    fun login() {
        if (_uiState.value.email.isBlank() || _uiState.value.password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Заполните все поля")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = loginUseCase(_uiState.value.email, _uiState.value.password)
            
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false, isLoggedIn = true)
                    _events.value = LoginEvent.NavigateToDashboard
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
                    _events.value = LoginEvent.ShowError
                }
            )
        }
    }

    fun register() {
        if (_uiState.value.email.isBlank() || _uiState.value.password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Заполните все поля")
            return
        }

        if (_uiState.value.password != _uiState.value.confirmPassword) {
            _uiState.value = _uiState.value.copy(error = "Пароли не совпадают")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = registerUseCase(_uiState.value.email, _uiState.value.password)
            
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false, isLoggedIn = true)
                    _events.value = LoginEvent.NavigateToDashboard
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
                    _events.value = LoginEvent.ShowError
                }
            )
        }
    }

    fun telegramAuth(initData: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = telegramAuthUseCase(initData)
            
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false, isLoggedIn = true)
                    _events.value = LoginEvent.NavigateToDashboard
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
                    _events.value = LoginEvent.ShowError
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun consumeEvent() {
        _events.value = null
    }
}
