package com.calorietracker.ui.screens.addfood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calorietracker.domain.model.MealType
import com.calorietracker.domain.model.Product
import com.calorietracker.domain.usecase.diary.AddDiaryEntryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class AddFoodUiState(
    val selectedProduct: Product? = null,
    val weightGrams: Int = 100,
    val selectedMealType: MealType = MealType.LUNCH,
    val date: LocalDate = LocalDate.now(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showSuccessMessage: Boolean = false
)

sealed class AddFoodEvent {
    object NavigateBack : AddFoodEvent()
    object ShowError : AddFoodEvent()
}

@HiltViewModel
class AddFoodViewModel @Inject constructor(
    private val addDiaryEntryUseCase: AddDiaryEntryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddFoodUiState())
    val uiState: StateFlow<AddFoodUiState> = _uiState.asStateFlow()

    private val _events = MutableStateFlow<AddFoodEvent?>(null)
    val events: StateFlow<AddFoodEvent?> = _events.asStateFlow()

    fun selectProduct(product: Product) {
        _uiState.value = _uiState.value.copy(selectedProduct = product)
    }

    fun onWeightChange(weight: Int) {
        if (weight in 1..10000) {
            _uiState.value = _uiState.value.copy(weightGrams = weight)
        }
    }

    fun onMealTypeChange(mealType: MealType) {
        _uiState.value = _uiState.value.copy(selectedMealType = mealType)
    }

    fun onDateChange(date: LocalDate) {
        _uiState.value = _uiState.value.copy(date = date)
    }

    fun saveEntry() {
        val product = _uiState.value.selectedProduct ?: run {
            _uiState.value = _uiState.value.copy(error = "Выберите продукт")
            return
        }

        if (_uiState.value.weightGrams <= 0) {
            _uiState.value = _uiState.value.copy(error = "Введите корректный вес")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val result = addDiaryEntryUseCase(
                productId = product.id,
                weightGrams = _uiState.value.weightGrams,
                mealType = _uiState.value.selectedMealType,
                date = _uiState.value.date
            )
            
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        showSuccessMessage = true
                    )
                    _events.value = AddFoodEvent.NavigateBack
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
                    _events.value = AddFoodEvent.ShowError
                }
            )
        }
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
