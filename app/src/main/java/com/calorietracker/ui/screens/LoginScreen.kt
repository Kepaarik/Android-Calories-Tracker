package com.calorietracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.isSystemInDarkTheme
import com.calorietracker.ui.components.GlassCard
import com.calorietracker.ui.components.GlassButton
import com.calorietracker.ui.components.GlassTextField
import com.calorietracker.ui.components.ButtonVariant
import com.calorietracker.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLogin by remember { mutableStateOf(true) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0088CC).copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = Alignment.TopCenter,
                    radius = 400f
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                darkTheme = isDarkTheme,
                padding = "32dp"
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = if (isLogin) "Вход" else "Регистрация",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
                    )
                    
                    Text(
                        text = if (isLogin) "Войдите в свой аккаунт" else "Создайте новый аккаунт",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDarkTheme) DarkTextSecondary else LightTextSecondary
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    GlassTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "Email",
                        darkTheme = isDarkTheme,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    GlassTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "Пароль",
                        darkTheme = isDarkTheme,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    GlassButton(
                        onClick = onLoginSuccess,
                        text = if (isLogin) "Войти" else "Зарегистрироваться",
                        darkTheme = isDarkTheme,
                        variant = ButtonVariant.Success,
                        fullWidth = true
                    )
                    
                    GlassButton(
                        onClick = { isLogin = !isLogin },
                        text = if (isLogin) "Нет аккаунта? Зарегистрироваться" else "Уже есть аккаунт? Войти",
                        darkTheme = isDarkTheme,
                        variant = ButtonVariant.Default,
                        fullWidth = true
                    )
                }
            }
        }
    }
}
