package com.calorietracker.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object AddFood : Screen("add_food")
    object ScanBarcode : Screen("scan_barcode")
    object Activity : Screen("activity")
    object Settings : Screen("settings")
    object History : Screen("history/{date}") {
        fun createRoute(date: String) = "history/$date"
    }
}
