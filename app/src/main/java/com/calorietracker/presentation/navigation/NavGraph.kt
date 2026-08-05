package com.calorietracker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    val authState by authViewModel.authState.collectAsState()
    
    val startDestination = when {
        authState.isLoading -> Screen.Splash.route
        authState.isLoggedIn -> Screen.Dashboard.route
        else -> Screen.Login.route
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(
                onTimeout = {
                    if (authState.isLoggedIn) {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
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
        
        // Dashboard Screen
        composable(Screen.Dashboard.route) {
            DashboardScreen()
        }
        
        // Products Screen
        composable(Screen.Products.route) {
            ProductsScreen(
                onProductSelected = { product ->
                    // TODO: Show dialog to add product to diary
                }
            )
        }
        
        // Statistics Screen
        composable(Screen.Statistics.route) {
            StatisticsScreen()
        }
        
        // Profile Screen
        composable(Screen.Profile.route) {
            ProfileScreen(
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
