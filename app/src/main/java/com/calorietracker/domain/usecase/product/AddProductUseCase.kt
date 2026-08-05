package com.calorietracker.domain.usecase.product

import com.calorietracker.domain.model.Product
import com.calorietracker.domain.repository.ProductRepository
import javax.inject.Inject

class AddProductUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(
        name: String,
        caloriesPer100g: Double,
        proteinsPer100g: Double,
        fatsPer100g: Double,
        carbsPer100g: Double,
        barcode: String? = null
    ): Result<Product> {
        return productRepository.createProduct(
            name = name,
            caloriesPer100g = caloriesPer100g,
            proteinsPer100g = proteinsPer100g,
            fatsPer100g = fatsPer100g,
            carbsPer100g = carbsPer100g,
            barcode = barcode
        )
    }
}
