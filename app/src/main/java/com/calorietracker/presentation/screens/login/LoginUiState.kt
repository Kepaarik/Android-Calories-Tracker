package com.calorietracker.presentation.screens.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val username: String? = null,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isRegistrationMode: Boolean = false,
    val error: String? = null
)
