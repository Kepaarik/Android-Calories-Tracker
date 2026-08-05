package com.calorietracker.presentation.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.calorietracker.domain.model.ActivityLevel
import com.calorietracker.domain.model.Gender
import com.calorietracker.presentation.components.common.GlassButton
import com.calorietracker.presentation.components.common.ErrorScreen
import com.calorietracker.presentation.components.common.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Handle logout state
    if (uiState.isLoggedOut) {
        onLogout()
        return
    }
    
    when {
        uiState.isLoading && uiState.user == null -> {
            LoadingIndicator(message = "Загрузка профиля...")
        }
        uiState.error != null -> {
            ErrorScreen(
                message = uiState.error!!,
                onRetry = viewModel::loadUserProfile
            )
        }
        else -> {
            ProfileContent(
                user = uiState.user,
                calorieNorm = uiState.calorieNorm,
                onSaveClick = viewModel::saveProfile,
                onLogoutClick = viewModel::logout,
                onNameChange = viewModel::onNameChange,
                onAgeChange = viewModel::onAgeChange,
                onGenderChange = viewModel::onGenderChange,
                onHeightChange = viewModel::onHeightChange,
                onWeightChange = viewModel::onWeightChange,
                onActivityLevelChange = viewModel::onActivityLevelChange
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileContent(
    userProfile: UserProfileUiModel?,
    calorieNorm: Int?,
    onSaveClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var name by remember { mutableStateOf(userProfile?.name ?: "") }
    var age by remember { mutableStateOf(userProfile?.age?.toString() ?: "") }
    var gender by remember { mutableStateOf(userProfile?.gender ?: Gender.MALE) }
    var height by remember { mutableStateOf(userProfile?.height?.toString() ?: "") }
    var weight by remember { mutableStateOf(userProfile?.weight?.toString() ?: "") }
    var activityLevel by remember { mutableStateOf(userProfile?.activityLevel ?: ActivityLevel.SEDENTARY) }
    
    var genderExpanded by remember { mutableStateOf(false) }
    var activityExpanded by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Профиль",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        
        if (calorieNorm != null) {
            Text(
                text = "Суточная норма: $calorieNorm ккал",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
        
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Имя") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = age,
                onValueChange = { age = it.filter { char -> char.isDigit() } },
                label = { Text("Возраст") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            
            ExposedDropdownMenuBox(
                expanded = genderExpanded,
                onExpandedChange = { genderExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = gender.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Пол") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    singleLine = true
                )
                
                ExposedDropdownMenu(
                    expanded = genderExpanded,
                    onDismissRequest = { genderExpanded = false }
                ) {
                    Gender.values().forEach { g ->
                        DropdownMenuItem(
                            text = { Text(g.displayName) },
                            onClick = {
                                gender = g
                                genderExpanded = false
                            }
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = height,
                onValueChange = { height = it.filter { char -> char.isDigit() } },
                label = { Text("Рост (см)") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it.filter { char -> char.isDigit() || char == '.' } },
                label = { Text("Вес (кг)") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ExposedDropdownMenuBox(
            expanded = activityExpanded,
            onExpandedChange = { activityExpanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = activityLevel.displayName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Уровень активности") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = activityExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                singleLine = true
            )
            
            ExposedDropdownMenu(
                expanded = activityExpanded,
                onDismissRequest = { activityExpanded = false }
            ) {
                ActivityLevel.values().forEach { level ->
                    DropdownMenuItem(
                        text = { Text(level.displayName) },
                        onClick = {
                            activityLevel = level
                            activityExpanded = false
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        GlassButton(
            onClick = onSaveClick,
            text = "Сохранить",
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        GlassButton(
            onClick = onLogoutClick,
            text = "Выйти",
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
            contentColor = MaterialTheme.colorScheme.error
        )
    }
}

private val Gender.displayName: String
    get() = when (this) {
        Gender.MALE -> "Мужской"
        Gender.FEMALE -> "Женский"
    }

private val ActivityLevel.displayName: String
    get() = when (this) {
        ActivityLevel.SEDENTARY -> "Сидячий"
        ActivityLevel.LIGHTLY_ACTIVE -> "Лёгкая активность"
        ActivityLevel.MODERATELY_ACTIVE -> "Умеренная активность"
        ActivityLevel.VERY_ACTIVE -> "Высокая активность"
        ActivityLevel.EXTRA_ACTIVE -> "Очень высокая активность"
    }
