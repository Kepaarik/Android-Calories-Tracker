package com.calorietracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calorietracker.domain.usecase.auth.LoginUseCase
import com.calorietracker.domain.usecase.auth.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun checkAuthStatus() {
        // TODO: Check if user is logged in from DataStore
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = false, isLoggedIn = false)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, errorMessage = null)
            
            loginUseCase(email, password)
                .onSuccess { user ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        user = user
                    )
                }
                .onFailure { error ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Ошибка входа"
                    )
                }
        }
    }

    fun register(email: String, password: String, name: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, errorMessage = null)
            
            registerUseCase(email, password, name)
                .onSuccess { user ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        user = user
                    )
                }
                .onFailure { error ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Ошибка регистрации"
                    )
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            // TODO: Clear token from DataStore
            _authState.value = AuthState(isLoggedIn = false)
        }
    }

    fun clearError() {
        _authState.value = _authState.value.copy(errorMessage = null)
    }
}

data class AuthState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val user: com.calorietracker.domain.model.User? = null,
    val errorMessage: String? = null
)
