package com.calorietracker.presentation.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calorietracker.data.local.preferences.AuthPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authPreferences: AuthPreferences
) : ViewModel() {

    suspend fun checkAuthStatus(): Boolean {
        return authPreferences.isLoggedIn()
    }
}
