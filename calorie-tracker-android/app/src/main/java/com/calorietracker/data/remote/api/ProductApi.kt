package com.calorietracker.data.remote.api

import com.calorietracker.data.remote.dto.ProductDto
import retrofit2.Response
import retrofit2.http.*

interface ProductApi {

    @GET("api/products/search")
    suspend fun searchProducts(@Query("query") query: String): Response<List<ProductDto>>

    @GET("api/products/{id}")
    suspend fun getProductById(@Path("id") id: Int): Response<ProductDto>

    @GET("api/products/barcode/{barcode}")
    suspend fun getProductByBarcode(@Path("barcode") barcode: String): Response<ProductDto>

    @POST("api/products")
    suspend fun addProduct(@Body product: ProductDto): Response<ProductDto>

    @PUT("api/products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body product: ProductDto
    ): Response<ProductDto>
}
