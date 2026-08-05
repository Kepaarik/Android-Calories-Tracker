package com.calorietracker.domain.usecase.product

import com.calorietracker.domain.model.Product
import com.calorietracker.domain.repository.ProductRepository

class AddProductUseCase(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(product: Product): Result<Product> {
        return productRepository.addProduct(product)
    }
}
