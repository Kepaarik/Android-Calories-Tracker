package com.calorietracker.data.remote.api

import com.calorietracker.data.remote.dto.*
import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.*

interface DiaryApi {
    
    @GET("diary")
    suspend fun getEntries(
        @Header("Authorization") token: String,
        @Query("date") date: String
    ): Response<List<DiaryEntryDto>>
    
    @POST("diary")
    suspend fun addEntry(
        @Header("Authorization") token: String,
        @Body request: CreateDiaryEntryRequestDto
    ): Response<DiaryEntryDto>
    
    @PUT("diary/{id}")
    suspend fun updateEntry(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: UpdateDiaryEntryRequestDto
    ): Response<DiaryEntryDto>
    
    @DELETE("diary/{id}")
    suspend fun deleteEntry(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>
    
    @GET("diary/summary")
    suspend fun getDailySummary(
        @Header("Authorization") token: String,
        @Query("date") date: String
    ): Response<DailySummaryDto>
}

@JsonClass(generateAdapter = true)
data class DailySummaryDto(
    @Json(name = "date") val date: String,
    @Json(name = "total_calories") val totalCalories: Double,
    @Json(name = "total_proteins") val totalProteins: Double,
    @Json(name = "total_fats") val totalFats: Double,
    @Json(name = "total_carbs") val totalCarbs: Double,
    @Json(name = "goal_calories") val goalCalories: Int,
    @Json(name = "goal_proteins") val goalProteins: Double,
    @Json(name = "goal_fats") val goalFats: Double,
    @Json(name = "goal_carbs") val goalCarbs: Double
)
