package com.calorietracker.data.repository

import com.calorietracker.data.mapper.ProductMapper.toDomain
import com.calorietracker.data.remote.NetworkResult
import com.calorietracker.data.remote.api.ProductApi
import com.calorietracker.data.remote.safeApiCall
import com.calorietracker.domain.model.Product
import com.calorietracker.domain.repository.ProductRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val productApi: ProductApi
) : ProductRepository {

    override suspend fun searchProducts(
        query: String,
        page: Int,
        limit: Int
    ): Result<List<Product>> {
        val token = getToken() ?: return Result.failure(Exception("Not authenticated"))
        
        return when (val result = safeApiCall {
            productApi.searchProducts("Bearer $token", query, page, limit)
        }) {
            is NetworkResult.Success -> {
                val products = result.data.products.map { it.toDomain() }
                Result.success(products)
            }
            is NetworkResult.Error -> {
                Result.failure(Exception("Search failed: ${result.message}"))
            }
            is NetworkResult.Loading -> {
                Result.failure(Exception("Loading..."))
            }
        }
    }

    override suspend fun getProductById(id: Int): Result<Product> {
        val token = getToken() ?: return Result.failure(Exception("Not authenticated"))
        
        return when (val result = safeApiCall {
            productApi.getProductById("Bearer $token", id)
        }) {
            is NetworkResult.Success -> {
                Result.success(result.data.toDomain())
            }
            is NetworkResult.Error -> {
                Result.failure(Exception("Failed to get product: ${result.message}"))
            }
            is NetworkResult.Loading -> {
                Result.failure(Exception("Loading..."))
            }
        }
    }

    override suspend fun getProductByBarcode(barcode: String): Result<Product> {
        val token = getToken() ?: return Result.failure(Exception("Not authenticated"))
        
        return when (val result = safeApiCall {
            productApi.getProductByBarcode("Bearer $token", barcode)
        }) {
            is NetworkResult.Success -> {
                Result.success(result.data.toDomain())
            }
            is NetworkResult.Error -> {
                Result.failure(Exception("Product not found: ${result.message}"))
            }
            is NetworkResult.Loading -> {
                Result.failure(Exception("Loading..."))
            }
        }
    }

    override suspend fun createProduct(
        name: String,
        caloriesPer100g: Double,
        proteinsPer100g: Double,
        fatsPer100g: Double,
        carbsPer100g: Double,
        barcode: String?
    ): Result<Product> {
        val token = getToken() ?: return Result.failure(Exception("Not authenticated"))
        
        val request = com.calorietracker.data.remote.dto.CreateProductRequestDto(
            name = name,
            caloriesPer100g = caloriesPer100g,
            proteinsPer100g = proteinsPer100g,
            fatsPer100g = fatsPer100g,
            carbsPer100g = carbsPer100g,
            barcode = barcode
        )
        
        return when (val result = safeApiCall {
            productApi.createProduct("Bearer $token", request)
        }) {
            is NetworkResult.Success -> {
                Result.success(result.data.toDomain())
            }
            is NetworkResult.Error -> {
                Result.failure(Exception("Failed to create product: ${result.message}"))
            }
            is NetworkResult.Loading -> {
                Result.failure(Exception("Loading..."))
            }
        }
    }

    private fun getToken(): String? {
        // This should be retrieved from DataStore or SharedPreferences
        return null
    }
}
