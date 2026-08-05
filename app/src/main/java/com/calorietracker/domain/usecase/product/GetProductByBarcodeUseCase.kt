package com.calorietracker.domain.usecase.product

import com.calorietracker.domain.model.Product
import com.calorietracker.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductByBarcodeUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(barcode: String): Result<Product> {
        return productRepository.getProductByBarcode(barcode)
    }
}
