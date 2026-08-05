package com.calorietracker.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calorietracker.data.local.preferences.AuthPreferences
import com.calorietracker.domain.model.ActivityLevel
import com.calorietracker.domain.model.Gender
import com.calorietracker.domain.model.User
import com.calorietracker.domain.usecase.user.CalculateCalorieNormUseCase
import com.calorietracker.domain.usecase.user.GetUserProfileUseCase
import com.calorietracker.domain.usecase.user.UpdateUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val calculateCalorieNormUseCase: CalculateCalorieNormUseCase,
    private val authPreferences: AuthPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            getUserProfileUseCase()
                .onSuccess { user ->
                    val calorieNorm = calculateCalorieNormUseCase(
                        gender = user.gender,
                        weightKg = user.weightKg,
                        heightCm = user.heightCm,
                        age = user.age,
                        activityLevel = user.activityLevel
                    )
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        user = user,
                        calorieNorm = calorieNorm,
                        originalUser = user,
                        error = null
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to load profile"
                    )
                }
        }
    }

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(
            user = _uiState.value.user?.copy(name = name)
        )
    }

    fun onAgeChange(age: Int) {
        _uiState.value = _uiState.value.copy(
            user = _uiState.value.user?.copy(age = age)
        )
    }

    fun onGenderChange(gender: Gender) {
        _uiState.value = _uiState.value.copy(
            user = _uiState.value.user?.copy(gender = gender)
        )
    }

    fun onHeightChange(heightCm: Double) {
        _uiState.value = _uiState.value.copy(
            user = _uiState.value.user?.copy(heightCm = heightCm)
        )
    }

    fun onWeightChange(weightKg: Double) {
        _uiState.value = _uiState.value.copy(
            user = _uiState.value.user?.copy(weightKg = weightKg)
        )
    }

    fun onActivityLevelChange(activityLevel: ActivityLevel) {
        _uiState.value = _uiState.value.copy(
            user = _uiState.value.user?.copy(activityLevel = activityLevel)
        )
    }

    fun saveProfile() {
        val state = _uiState.value
        val user = state.user ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            
            updateUserProfileUseCase(user)
                .onSuccess { updatedUser ->
                    val calorieNorm = calculateCalorieNormUseCase(
                        gender = updatedUser.gender,
                        weightKg = updatedUser.weightKg,
                        heightCm = updatedUser.heightCm,
                        age = updatedUser.age,
                        activityLevel = updatedUser.activityLevel
                    )
                    
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        user = updatedUser,
                        originalUser = updatedUser,
                        calorieNorm = calorieNorm,
                        hasUnsavedChanges = false,
                        error = null
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = exception.message ?: "Failed to save profile"
                    )
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authPreferences.clearAuthData()
            _uiState.value = _uiState.value.copy(isLoggedOut = true)
        }
    }

    fun checkUnsavedChanges() {
        val state = _uiState.value
        val original = state.originalUser
        val current = state.user
        
        val hasChanges = original != null && current != null && 
            (original.name != current.name ||
             original.age != current.age ||
             original.gender != current.gender ||
             original.heightCm != current.heightCm ||
             original.weightKg != current.weightKg ||
             original.activityLevel != current.activityLevel)
        
        _uiState.value = _uiState.value.copy(hasUnsavedChanges = hasChanges)
    }

    fun discardChanges() {
        val originalUser = _uiState.value.originalUser
        if (originalUser != null) {
            _uiState.value = _uiState.value.copy(
                user = originalUser,
                hasUnsavedChanges = false
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
