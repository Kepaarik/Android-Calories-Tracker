package com.calorietracker.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object Products : Screen("products")
    object Statistics : Screen("statistics")
    object Profile : Screen("profile")
    object WeightHistory : Screen("weight_history")
}
