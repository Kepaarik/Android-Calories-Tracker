package com.calorietracker.presentation.components.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.calorietracker.presentation.navigation.Screen

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    val items = listOf(
        BottomNavItem(Screen.Dashboard.route, "Дневник", Icons.Default.Home),
        BottomNavItem(Screen.Products.route, "Продукты", Icons.Default.Search),
        BottomNavItem(Screen.Statistics.route, "Статистика", Icons.Default.ShowChart),
        BottomNavItem(Screen.Profile.route, "Профиль", Icons.Default.Person)
    )
    
    NavigationBar(modifier = modifier) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) }
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)
