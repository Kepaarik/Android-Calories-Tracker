package com.calorietracker.ui.screens.scanbarcode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calorietracker.domain.model.Product
import com.calorietracker.domain.usecase.product.GetProductByBarcodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScanBarcodeUiState(
    val barcode: String = "",
    val product: Product? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isCameraPermissionGranted: Boolean = false,
    val showProductDialog: Boolean = false
)

sealed class ScanBarcodeEvent {
    object NavigateBack : ScanBarcodeEvent()
    object ShowError : ScanBarcodeEvent()
    object RequestCameraPermission : ScanBarcodeEvent()
}

@HiltViewModel
class ScanBarcodeViewModel @Inject constructor(
    private val getProductByBarcodeUseCase: GetProductByBarcodeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScanBarcodeUiState())
    val uiState: StateFlow<ScanBarcodeUiState> = _uiState.asStateFlow()

    private val _events = MutableStateFlow<ScanBarcodeEvent?>(null)
    val events: StateFlow<ScanBarcodeEvent?> = _events.asStateFlow()

    fun onBarcodeScanned(barcode: String) {
        if (barcode.isBlank()) return
        
        _uiState.value = _uiState.value.copy(barcode = barcode, isLoading = true, error = null)
        lookupProduct(barcode)
    }

    private fun lookupProduct(barcode: String) {
        viewModelScope.launch {
            val result = getProductByBarcodeUseCase(barcode)
            
            result.fold(
                onSuccess = { product ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        product = product,
                        showProductDialog = product != null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Продукт не найден"
                    )
                    _events.value = ScanBarcodeEvent.ShowError
                }
            )
        }
    }

    fun dismissProductDialog() {
        _uiState.value = _uiState.value.copy(showProductDialog = false)
    }

    fun checkCameraPermission() {
        // This should be handled by the Activity/Compose permission handler
        _events.value = ScanBarcodeEvent.RequestCameraPermission
    }

    fun onPermissionResult(granted: Boolean) {
        _uiState.value = _uiState.value.copy(isCameraPermissionGranted = granted)
        if (!granted) {
            _uiState.value = _uiState.value.copy(error = "Требуется разрешение камеры для сканирования")
        }
    }

    fun consumeEvent() {
        _events.value = null
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
