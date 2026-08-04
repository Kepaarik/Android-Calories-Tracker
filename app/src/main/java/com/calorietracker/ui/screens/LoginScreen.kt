package com.calorietracker.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calorietracker.data.repository.TelegramAuthRepository
import com.calorietracker.ui.components.GlassCard
import com.calorietracker.ui.components.GlassTextField
import com.calorietracker.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.unit.LayoutDirection

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val isDarkTheme = isSystemInDarkTheme()
    
    // Telegram Web View launcher
    val telegramLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == androidx.activity.ComponentActivity.RESULT_OK) {
            viewModel.processTelegramAuth()
        }
    }
    
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
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            darkTheme = isDarkTheme,
            padding = "32px"
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Вход",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
                )
                
                Text(
                    text = "Войдите в свой аккаунт",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDarkTheme) DarkTextSecondary else LightTextSecondary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                GlassTextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    placeholder = "your@email.com",
                    darkTheme = isDarkTheme
                )
                
                GlassTextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    placeholder = "••••••••",
                    darkTheme = isDarkTheme
                )
                
                com.calorietracker.ui.components.GlassButton(
                    onClick = {
                        val telegramUrl = "https://t.me/your_bot_name?start=auth"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(telegramUrl))
                        telegramLauncher.launch(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    text = "Войти",
                    darkTheme = isDarkTheme,
                    variant = com.calorietracker.ui.components.ButtonVariant.Success,
                    fullWidth = true
                )
                
                TextButton(
                    onClick = {
                        viewModel.loginAsGuest()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Войти как гость (для тестирования)",
                        color = if (isDarkTheme) DarkPrimary else LightPrimary
                    )
                }
                
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
    
    fun loginAsGuest() {
        viewModelScope.launch {
            authRepository.login()
        }
    }
    
    fun processTelegramAuth() {
        viewModelScope.launch {
            authRepository.login()
        }
    }
}
