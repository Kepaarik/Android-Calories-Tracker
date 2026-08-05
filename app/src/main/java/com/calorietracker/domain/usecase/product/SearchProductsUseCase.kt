package com.calorietracker.domain.usecase.product

import com.calorietracker.domain.model.Product
import com.calorietracker.domain.repository.ProductRepository
import javax.inject.Inject

class SearchProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(
        query: String,
        page: Int = 1,
        limit: Int = 20
    ): Result<List<Product>> {
        return productRepository.searchProducts(query, page, limit)
    }
}
