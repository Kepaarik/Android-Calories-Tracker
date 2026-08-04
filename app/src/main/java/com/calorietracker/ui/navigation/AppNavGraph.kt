package com.calorietracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.calorietracker.ui.screens.LoginScreen
import com.calorietracker.ui.screens.HomeScreen
import com.calorietracker.ui.screens.ProfileScreen
import com.calorietracker.ui.screens.AddFoodScreen
import com.calorietracker.ui.screens.ScanBarcodeScreen
import com.calorietracker.ui.screens.ActivityScreen
import com.calorietracker.ui.screens.SettingsScreen
import com.calorietracker.ui.screens.HistoryScreen

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier
) {
    val navController = androidx.navigation.compose.rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToAddFood = { navController.navigate(Screen.AddFood.route) },
                onNavigateToScanBarcode = { navController.navigate(Screen.ScanBarcode.route) },
                onNavigateToActivity = { navController.navigate(Screen.Activity.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToHistory = { date -> 
                    navController.navigate(Screen.History.createRoute(date)) 
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AddFood.route) {
            AddFoodScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToScanBarcode = { navController.navigate(Screen.ScanBarcode.route) }
            )
        }

        composable(Screen.ScanBarcode.route) {
            ScanBarcodeScreen(
                onNavigateBack = { navController.popBackStack() },
                onBarcodeScanned = { barcode ->
                    // Handle scanned barcode
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Activity.route) {
            ActivityScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.History.route,
            arguments = listOf(
                navArgument("date") { type = NavType.StringType }
            )
        ) {
            HistoryScreen(
                date = it.arguments?.getString("date") ?: "",
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
