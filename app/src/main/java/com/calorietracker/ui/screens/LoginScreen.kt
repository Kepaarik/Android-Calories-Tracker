package com.calorietracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calorietracker.data.repository.TelegramAuthRepository
import com.calorietracker.ui.components.GlassCard
import com.calorietracker.ui.components.GlassTextField
import com.calorietracker.ui.components.GlassButton
import com.calorietracker.ui.components.ButtonVariant
import com.calorietracker.ui.theme.DarkPrimary
import com.calorietracker.ui.theme.DarkTextPrimary
import com.calorietracker.ui.theme.DarkTextSecondary
import com.calorietracker.ui.theme.LightPrimary
import com.calorietracker.ui.theme.LightTextPrimary
import com.calorietracker.ui.theme.LightTextSecondary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val isDarkTheme = isSystemInDarkTheme()
    
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            onLoginSuccess()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0088CC).copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = androidx.compose.ui.geometry.Offset(200f, 300f),
                    radius = 600f
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            darkTheme = isDarkTheme,
            padding = "32px"
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (viewModel.isLoginMode.value) "Вход" else "Регистрация",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
                )
                
                Text(
                    text = if (viewModel.isLoginMode.value) "Войдите в свой аккаунт" else "Создайте новый аккаунт",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDarkTheme) DarkTextSecondary else LightTextSecondary
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                GlassTextField(
                    value = viewModel.email.value,
                    onValueChange = { viewModel.setEmail(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    placeholder = "your@email.com",
                    darkTheme = isDarkTheme
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                GlassTextField(
                    value = viewModel.password.value,
                    onValueChange = { viewModel.setPassword(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    placeholder = "••••••••",
                    darkTheme = isDarkTheme
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                if (viewModel.error.value != null) {
                    Text(
                        text = viewModel.error.value!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFEF5350),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                GlassButton(
                    onClick = {
                        if (viewModel.isLoginMode.value) {
                            viewModel.login()
                        } else {
                            viewModel.register()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    text = if (viewModel.isLoginMode.value) "Войти" else "Зарегистрироваться",
                    darkTheme = isDarkTheme,
                    variant = ButtonVariant.Success,
                    fullWidth = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TextButton(
                    onClick = {
                        viewModel.toggleMode()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (viewModel.isLoginMode.value) 
                            "Нет аккаунта? Зарегистрироваться" 
                        else 
                            "Уже есть аккаунт? Войти",
                        color = if (isDarkTheme) DarkPrimary else LightPrimary
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Для входа используется ваш аккаунт Telegram",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDarkTheme) DarkTextSecondary else LightTextSecondary
                )
            }
        }
    }
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: TelegramAuthRepository
) : ViewModel() {
    
    val isLoggedIn = authRepository.isLoggedIn
    
    private val _isLoginMode = kotlinx.coroutines.flow.MutableStateFlow(true)
    val isLoginMode = _isLoginMode.asStateFlow()
    
    private val _email = kotlinx.coroutines.flow.MutableStateFlow("")
    val email = _email.asStateFlow()
    
    private val _password = kotlinx.coroutines.flow.MutableStateFlow("")
    val password = _password.asStateFlow()
    
    private val _error = kotlinx.coroutines.flow.MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    
    fun setEmail(value: String) {
        _email.value = value
        _error.value = null
    }
    
    fun setPassword(value: String) {
        _password.value = value
        _error.value = null
    }
    
    fun toggleMode() {
        _isLoginMode.value = !_isLoginMode.value
        _error.value = null
    }
    
    fun login() {
        if (_email.value.isBlank() || _password.value.isEmpty()) {
            _error.value = "Заполните все поля"
            return
        }
        viewModelScope.launch {
            try {
                authRepository.login()
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка входа"
            }
        }
    }
    
    fun register() {
        if (_email.value.isBlank() || _password.value.isEmpty()) {
            _error.value = "Заполните все поля"
            return
        }
        viewModelScope.launch {
            try {
                authRepository.login()
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка регистрации"
            }
        }
    }
    
    fun loginAsGuest() {
        viewModelScope.launch {
            authRepository.login()
        }
    }
}
