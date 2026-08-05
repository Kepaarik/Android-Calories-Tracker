package com.calorietracker.domain.repository

import com.calorietracker.domain.model.Product

interface ProductRepository {
    suspend fun searchProducts(query: String, page: Int = 1, limit: Int = 20): Result<List<Product>>
    suspend fun getProductById(id: Int): Result<Product>
    suspend fun getProductByBarcode(barcode: String): Result<Product>
    suspend fun createProduct(
        name: String,
        caloriesPer100g: Double,
        proteinsPer100g: Double,
        fatsPer100g: Double,
        carbsPer100g: Double,
        barcode: String? = null
    ): Result<Product>
}
