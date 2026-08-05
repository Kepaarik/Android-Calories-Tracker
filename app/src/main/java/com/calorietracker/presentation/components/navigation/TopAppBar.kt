package com.calorietracker.presentation.components.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.calorietracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalorieTrackerTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {},
    onThemeToggle: () -> Unit = {}
) {
    TopAppBar(
        title = { Text(title) },
        modifier = modifier,
        actions = {
            IconButton(onClick = onThemeToggle) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_theme),
                    contentDescription = stringResource(id = R.string.toggle_theme)
                )
            }
            IconButton(onClick = onProfileClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_profile),
                    contentDescription = stringResource(id = R.string.profile)
                )
            }
        }
    )
}
