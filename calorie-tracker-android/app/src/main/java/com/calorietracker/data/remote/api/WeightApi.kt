package com.calorietracker.data.remote.api

import com.calorietracker.data.remote.dto.WeightEntryDto
import retrofit2.Response
import retrofit2.http.*

interface WeightApi {

    @GET("api/weight/{userId}")
    suspend fun getWeightHistory(@Path("userId") userId: Int): Response<List<WeightEntryDto>>

    @GET("api/weight/{userId}/range")
    suspend fun getWeightHistoryByDateRange(
        @Path("userId") userId: Int,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): Response<List<WeightEntryDto>>

    @POST("api/weight")
    suspend fun addWeightEntry(@Body entry: WeightEntryDto): Response<WeightEntryDto>

    @DELETE("api/weight/{id}")
    suspend fun deleteWeightEntry(@Path("id") id: Int): Response<Unit>
}
