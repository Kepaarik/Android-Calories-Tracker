package com.calorietracker.data.remote.api

import com.calorietracker.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ProductApi {
    
    @GET("products")
    suspend fun searchProducts(
        @Header("Authorization") token: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<SearchProductsResponseDto>
    
    @GET("products/{id}")
    suspend fun getProductById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<ProductDto>
    
    @GET("products/barcode/{barcode}")
    suspend fun getProductByBarcode(
        @Header("Authorization") token: String,
        @Path("barcode") barcode: String
    ): Response<ProductDto>
    
    @POST("products")
    suspend fun createProduct(
        @Header("Authorization") token: String,
        @Body request: CreateProductRequestDto
    ): Response<ProductDto>
}
