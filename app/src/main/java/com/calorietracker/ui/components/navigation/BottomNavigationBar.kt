package com.calorietracker.ui.components.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = "dashboard",
        label = "Дневник",
        icon = Icons.Default.Home,
        selectedIcon = Icons.Filled.Home
    ),
    BottomNavItem(
        route = "products",
        label = "Продукты",
        icon = Icons.Default.Search,
        selectedIcon = Icons.Filled.Search
    ),
    BottomNavItem(
        route = "statistics",
        label = "Статистика",
        icon = Icons.Default.BarChart,
        selectedIcon = Icons.Filled.BarChart
    ),
    BottomNavItem(
        route = "activity",
        label = "Активность",
        icon = Icons.Default.FitnessCenter,
        selectedIcon = Icons.Filled.FitnessCenter
    ),
    BottomNavItem(
        route = "profile",
        label = "Профиль",
        icon = Icons.Default.Person,
        selectedIcon = Icons.Filled.Person
    )
)

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar {
        bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = isSelected,
                onClick = { onNavigate(item.route) }
            )
        }
    }
}
