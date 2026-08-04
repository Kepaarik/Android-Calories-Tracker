package com.calorietracker.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calorietracker.data.repository.TelegramAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    
    // Telegram Web View launcher
    val telegramLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Handle Telegram auth result
        if (result.resultCode == androidx.activity.ComponentActivity.RESULT_OK) {
            // Process auth data
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
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Calorie Tracker",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Отслеживайте калории, активность и достигайте целей",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    // Launch Telegram authentication
                    val telegramUrl = "https://t.me/your_bot_name?start=auth"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(telegramUrl))
                    telegramLauncher.launch(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null
                    )
                    Text(
                        text = "Войти через Telegram",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            
            OutlinedButton(
                onClick = {
                    // Guest login for testing
                    viewModel.loginAsGuest()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Войти как гость (для тестирования)",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            Text(
                text = "Для входа используется ваш аккаунт Telegram",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
        // Process Telegram auth data from init data
        // In real implementation, parse initData from Telegram Web App
        viewModelScope.launch {
            // Simulate auth process
            authRepository.login()
        }
    }
}
