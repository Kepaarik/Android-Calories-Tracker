package com.calorietracker.presentation.screens.products

import com.calorietracker.domain.model.Product

data class ProductsUiState(
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val searchResults: List<Product> = emptyList(),
    val selectedProduct: Product? = null,
    val error: String? = null
)
