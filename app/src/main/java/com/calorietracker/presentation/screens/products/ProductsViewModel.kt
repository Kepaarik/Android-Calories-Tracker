package com.calorietracker.presentation.screens.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calorietracker.domain.model.Product
import com.calorietracker.domain.usecase.product.SearchProductsUseCase
import com.calorietracker.domain.usecase.product.AddProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val searchProductsUseCase: SearchProductsUseCase,
    private val addProductUseCase: AddProductUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductsUiState())
    val uiState: StateFlow<ProductsUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private val searchQueryFlow = MutableStateFlow("")

    init {
        // Observe search query changes with debounce
        viewModelScope.launch {
            searchQueryFlow
                .debounce(300)
                .distinctUntilChanged()
                .collect { searchTerm ->
                    if (searchTerm.length >= 2) {
                        searchProducts(searchTerm)
                    } else if (searchTerm.isEmpty()) {
                        clearResults()
                    }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        searchQueryFlow.value = query
    }

    private fun searchProducts(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            searchProductsUseCase(query, limit = 20)
                .onSuccess { products ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        searchResults = products,
                        error = null
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Search failed"
                    )
                }
        }
    }

    fun clearResults() {
        _uiState.value = _uiState.value.copy(
            searchResults = emptyList(),
            error = null
        )
    }

    fun selectProduct(product: Product) {
        _uiState.value = _uiState.value.copy(selectedProduct = product)
    }

    fun clearSelectedProduct() {
        _uiState.value = _uiState.value.copy(selectedProduct = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
