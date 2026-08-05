package com.calorietracker.ui.screens.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calorietracker.domain.model.Product
import com.calorietracker.domain.usecase.product.AddProductUseCase
import com.calorietracker.domain.usecase.product.GetProductByBarcodeUseCase
import com.calorietracker.domain.usecase.product.SearchProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductsUiState(
    val searchQuery: String = "",
    val products: List<Product> = emptyList(),
    val barcodeProduct: Product? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSearching: Boolean = false,
    val showAddDialog: Boolean = false,
    val selectedProduct: Product? = null
)

sealed class ProductsEvent {
    object NavigateBack : ProductsEvent()
    object ShowError : ProductsEvent()
    data class ProductAdded(val product: Product) : ProductsEvent()
}

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val searchProductsUseCase: SearchProductsUseCase,
    private val getProductByBarcodeUseCase: GetProductByBarcodeUseCase,
    private val addProductUseCase: AddProductUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductsUiState())
    val uiState: StateFlow<ProductsUiState> = _uiState.asStateFlow()

    private val _events = MutableStateFlow<ProductsEvent?>(null)
    val events: StateFlow<ProductsEvent?> = _events.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query, error = null)
    }

    fun searchProducts() {
        val query = _uiState.value.searchQuery.trim()
        if (query.isEmpty()) {
            _uiState.value = _uiState.value.copy(products = emptyList())
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, isSearching = true, error = null)
            val result = searchProductsUseCase(query)
            
            result.fold(
                onSuccess = { products ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSearching = false,
                        products = products
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, isSearching = false, error = error.message)
                    _events.value = ProductsEvent.ShowError
                }
            )
        }
    }

    fun scanBarcode(barcode: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = getProductByBarcodeUseCase(barcode)
            
            result.fold(
                onSuccess = { product ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        barcodeProduct = product,
                        showAddDialog = product != null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
                    _events.value = ProductsEvent.ShowError
                }
            )
        }
    }

    fun selectProduct(product: Product) {
        _uiState.value = _uiState.value.copy(selectedProduct = product, showAddDialog = true)
    }

    fun dismissAddDialog() {
        _uiState.value = _uiState.value.copy(showAddDialog = false, selectedProduct = null)
    }

    fun addProductToDiary(
        product: Product,
        weightGrams: Int,
        mealType: com.calorietracker.domain.model.MealType
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = addProductUseCase(product.id, weightGrams, mealType)
            
            result.fold(
                onSuccess = { entry ->
                    _uiState.value = _uiState.value.copy(isLoading = false, showAddDialog = false)
                    _events.value = ProductsEvent.ProductAdded(entry)
                    _events.value = ProductsEvent.NavigateBack
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
                    _events.value = ProductsEvent.ShowError
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
}
