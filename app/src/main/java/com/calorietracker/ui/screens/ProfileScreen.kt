package com.calorietracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.calorietracker.ui.components.*
import com.calorietracker.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit
) {
    val isDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (isDarkTheme) {
                        listOf(DarkBackground, DarkSurface)
                    } else {
                        listOf(LightBackground, LightSurface)
                    }
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .background(
                            color = if (isDarkTheme) DarkGlassBg else LightGlassBg,
                            shape = MaterialTheme.shapes.medium
                        )
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Назад",
                        tint = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
                    )
                }
                Text(
                    text = "Профиль",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
                )
                Spacer(modifier = Modifier.size(40.dp))
            }
            
            // User Info Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                darkTheme = isDarkTheme,
                padding = "24px"
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(android.R.drawable.sym_def_app_icon),
                        contentDescription = "Avatar",
                        modifier = Modifier.size(80.dp)
                    )
                    Text(
                        text = "Имя пользователя",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
                    )
                    Text(
                        text = "@telegram_username",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDarkTheme) DarkTextSecondary else LightTextSecondary
                    )
                }
            }
            
            // Settings
            Text(
                text = "Настройки",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
            )
            
            SettingsGlassItem(
                title = "Дневная цель калорий",
                subtitle = "2000 ккал",
                onClick = { },
                isDarkTheme = isDarkTheme
            )
            
            SettingsGlassItem(
                title = "Цель по шагам",
                subtitle = "10000 шагов",
                onClick = { },
                isDarkTheme = isDarkTheme
            )
            
            SettingsGlassItem(
                title = "Вес",
                subtitle = "75 кг",
                onClick = { },
                isDarkTheme = isDarkTheme
            )
            
            SettingsGlassItem(
                title = "Рост",
                subtitle = "180 см",
                onClick = { },
                isDarkTheme = isDarkTheme
            )
            
            SettingsGlassItem(
                title = "Возраст",
                subtitle = "25 лет",
                onClick = { },
                isDarkTheme = isDarkTheme
            )
        }
    }
}

@Composable
private fun SettingsGlassItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDarkTheme: Boolean
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        darkTheme = isDarkTheme,
        padding = "16px",
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDarkTheme) DarkTextSecondary else LightTextSecondary
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = if (isDarkTheme) DarkTextSecondary else LightTextSecondary
            )
        }
    }
}
