package com.calorietracker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.calorietracker.presentation.screens.dashboard.DashboardScreen
import com.calorietracker.presentation.screens.login.LoginScreen
import com.calorietracker.presentation.screens.profile.ProfileScreen
import com.calorietracker.presentation.screens.products.ProductsScreen
import com.calorietracker.presentation.screens.splash.SplashScreen
import com.calorietracker.presentation.screens.statistics.StatisticsScreen
import com.calorietracker.presentation.viewmodel.AuthViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Login Screen
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    // TODO: Navigate to register screen
                }
            )
        }
        
        // Dashboard Screen with Bottom Navigation
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToProducts = {
                    navController.navigate(Screen.Products.route)
                },
                onNavigateToStatistics = {
                    navController.navigate(Screen.Statistics.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }
        
        // Products Screen
        composable(Screen.Products.route) {
            ProductsScreen(
                onNavigateBack = { navController.popBackStack() },
                onProductSelected = { product ->
                    // TODO: Show dialog to add product to diary
                }
            )
        }
        
        // Statistics Screen
        composable(Screen.Statistics.route) {
            StatisticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Profile Screen
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
