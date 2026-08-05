package com.calorietracker.presentation.components.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalorieTrackerTopAppBar(
    title: String = "Calorie Tracker",
    onThemeToggle: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    TopAppBar(
        title = { Text(title) },
        actions = {
            IconButton(onClick = onThemeToggle) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Сменить тему"
                )
            }
            IconButton(onClick = onProfileClick) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Профиль"
                )
            }
        },
        modifier = modifier
    )
}
