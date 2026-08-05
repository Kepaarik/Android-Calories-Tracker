package com.calorietracker.domain.usecase.product

import com.calorietracker.domain.model.Product
import com.calorietracker.domain.repository.ProductRepository

class SearchProductsUseCase(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(query: String): List<Product> {
        return productRepository.searchProducts(query)
    }
}
