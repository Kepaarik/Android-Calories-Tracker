package com.calorietracker.presentation.screens.profile

import com.calorietracker.domain.model.ActivityLevel
import com.calorietracker.domain.model.Gender
import com.calorietracker.domain.model.User

data class ProfileUiState(
    val user: User? = null,
    val originalUser: User? = null,
    val calorieNorm: Double? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val hasUnsavedChanges: Boolean = false,
    val error: String? = null
)
