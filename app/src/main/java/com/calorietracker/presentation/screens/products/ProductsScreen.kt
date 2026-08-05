package com.calorietracker.presentation.screens.products

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.calorietracker.domain.model.Product
import com.calorietracker.presentation.components.common.EmptyState
import com.calorietracker.presentation.components.common.ErrorScreen
import com.calorietracker.presentation.components.common.GlassCard
import com.calorietracker.presentation.components.common.LoadingIndicator
import com.calorietracker.presentation.components.products.ProductListItem
import com.calorietracker.presentation.components.products.ProductSearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    onProductSelected: (Product) -> Unit,
    viewModel: ProductsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    when {
        uiState.isLoading && uiState.products.isEmpty() -> {
            LoadingIndicator(message = "Поиск продуктов...")
        }
        uiState.error != null -> {
            ErrorScreen(
                message = uiState.error!!,
                onRetry = { viewModel.searchProducts(uiState.searchQuery) }
            )
        }
        else -> {
            ProductsContent(
                searchQuery = uiState.searchQuery,
                products = uiState.products,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                onProductSelected = onProductSelected
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductsContent(
    searchQuery: String,
    products: List<Product>,
    onSearchQueryChange: (String) -> Unit,
    onProductSelected: (Product) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Продукты",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        ProductSearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            placeholder = "Например: яблоко, рис, курица"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (products.isEmpty() && searchQuery.isNotBlank()) {
            EmptyState(
                title = "Ничего не найдено",
                message = "Попробуйте изменить поисковый запрос"
            )
        } else if (products.isEmpty()) {
            EmptyState(
                title = "Поиск продуктов",
                message = "Введите название продукта для поиска"
            )
        } else {
            LazyColumn {
                items(products) { product ->
                    ProductListItem(
                        product = product,
                        onClick = { onProductSelected(product) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
