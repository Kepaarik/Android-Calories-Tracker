package com.calorietracker.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

data class ProfileUiState(
    val name: String = "",
    val age: Int = 30,
    val gender: Gender = Gender.MALE,
    val heightCm: Double = 170.0,
    val weightKg: Double = 70.0,
    val activityLevel: ActivityLevel = ActivityLevel.MODERATE,
    val calorieNorm: Int = 2000,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val showSuccessMessage: Boolean = false
)

sealed class ProfileEvent {
    object NavigateBack : ProfileEvent()
    object ShowError : ProfileEvent()
    object Logout : ProfileEvent()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val calculateCalorieNormUseCase: CalculateCalorieNormUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _events = MutableStateFlow<ProfileEvent?>(null)
    val events: StateFlow<ProfileEvent?> = _events.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = getUserProfileUseCase()
            
            result.fold(
                onSuccess = { user ->
                    val calorieNorm = calculateCalorieNormUseCase(
                        gender = user.gender,
                        weightKg = user.weightKg,
                        heightCm = user.heightCm,
                        age = user.age,
                        activityLevel = user.activityLevel
                    ).getOrNull() ?: 2000
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        name = user.name ?: "",
                        age = user.age,
                        gender = user.gender,
                        heightCm = user.heightCm,
                        weightKg = user.weightKg,
                        activityLevel = user.activityLevel,
                        calorieNorm = calorieNorm
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
                    _events.value = ProfileEvent.ShowError
                }
            )
        }
    }

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun onAgeChange(age: Int) {
        if (age in 1..120) {
            _uiState.value = _uiState.value.copy(age = age)
            recalculateCalorieNorm()
        }
    }

    fun onGenderChange(gender: Gender) {
        _uiState.value = _uiState.value.copy(gender = gender)
        recalculateCalorieNorm()
    }

    fun onHeightChange(height: Double) {
        if (height in 50.0..250.0) {
            _uiState.value = _uiState.value.copy(heightCm = height)
            recalculateCalorieNorm()
        }
    }

    fun onWeightChange(weight: Double) {
        if (weight in 20.0..300.0) {
            _uiState.value = _uiState.value.copy(weightKg = weight)
            recalculateCalorieNorm()
        }
    }

    fun onActivityLevelChange(activityLevel: ActivityLevel) {
        _uiState.value = _uiState.value.copy(activityLevel = activityLevel)
        recalculateCalorieNorm()
    }

    private fun recalculateCalorieNorm() {
        val state = _uiState.value
        val calorieNorm = calculateCalorieNormUseCase(
            gender = state.gender,
            weightKg = state.weightKg,
            heightCm = state.heightCm,
            age = state.age,
            activityLevel = state.activityLevel
        ).getOrNull() ?: 2000
        
        _uiState.value = _uiState.value.copy(calorieNorm = calorieNorm)
    }

    fun saveProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            
            val user = User(
                id = 0, // Will be set by backend
                name = _uiState.value.name.ifEmpty { null },
                email = "", // Email cannot be changed
                age = _uiState.value.age,
                gender = _uiState.value.gender,
                heightCm = _uiState.value.heightCm,
                weightKg = _uiState.value.weightKg,
                activityLevel = _uiState.value.activityLevel
            )
            
            val result = updateUserProfileUseCase(user)
            
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        showSuccessMessage = true
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(isSaving = false, error = error.message)
                    _events.value = ProfileEvent.ShowError
                }
            )
        }
    }

    fun logout() {
        _events.value = ProfileEvent.Logout
    }

    fun consumeEvent() {
        _events.value = null
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun dismissSuccessMessage() {
        _uiState.value = _uiState.value.copy(showSuccessMessage = false)
    }
}
