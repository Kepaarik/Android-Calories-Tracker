package com.calorietracker.presentation.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.calorietracker.presentation.components.common.GlassButton
import com.calorietracker.presentation.components.common.ErrorScreen
import com.calorietracker.presentation.components.common.LoadingIndicator

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    when {
        uiState.isLoading -> {
            LoadingIndicator(message = "Вход...")
        }
        uiState.errorMessage != null -> {
            ErrorScreen(
                message = uiState.errorMessage!!,
                onRetry = { viewModel.retry() }
            )
        }
        uiState.isLoggedIn -> {
            onLoginSuccess()
        }
        else -> {
            LoginContent(
                email = uiState.email,
                password = uiState.password,
                isLoading = uiState.isLoading,
                errorMessage = uiState.errorMessage,
                onEmailChange = viewModel::onEmailChange,
                onPasswordChange = viewModel::onPasswordChange,
                onLoginClick = viewModel::onLoginClick,
                onRegisterClick = onNavigateToRegister
            )
        }
    }
}

@Composable
private fun LoginContent(
    email: String,
    password: String,
    isLoading: Boolean,
    errorMessage: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Calorie Tracker",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Пароль") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            enabled = !isLoading
        )
        
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        GlassButton(
            onClick = onLoginClick,
            text = "Войти",
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        GlassButton(
            onClick = onRegisterClick,
            text = "Зарегистрироваться",
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            backgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
        )
    }
}
