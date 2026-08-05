package com.calorietracker.data.remote.api

import com.calorietracker.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface WeightApi {
    
    @GET("weight")
    suspend fun getWeightHistory(
        @Header("Authorization") token: String,
        @Query("from_date") fromDate: String?,
        @Query("to_date") toDate: String?
    ): Response<List<WeightEntryDto>>
    
    @POST("weight")
    suspend fun addWeightEntry(
        @Header("Authorization") token: String,
        @Body request: CreateWeightEntryRequestDto
    ): Response<WeightEntryDto>
    
    @DELETE("weight/{id}")
    suspend fun deleteWeightEntry(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>
}
