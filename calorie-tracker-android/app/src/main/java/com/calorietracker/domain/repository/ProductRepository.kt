package com.calorietracker.domain.repository

import com.calorietracker.domain.model.Product

interface ProductRepository {
    suspend fun searchProducts(query: String): List<Product>
    suspend fun getProductById(id: Int): Product?
    suspend fun getProductByBarcode(barcode: String): Product?
    suspend fun addProduct(product: Product): Result<Product>
    suspend fun updateProduct(product: Product): Result<Unit>
}
