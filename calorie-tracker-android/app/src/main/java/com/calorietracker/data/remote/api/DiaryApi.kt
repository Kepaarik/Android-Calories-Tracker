package com.calorietracker.data.remote.api

import com.calorietracker.data.remote.dto.DiaryEntryDto
import retrofit2.Response
import retrofit2.http.*

interface DiaryApi {

    @GET("api/diary/{date}")
    suspend fun getEntriesByDate(
        @Path("date") date: String
    ): Response<List<DiaryEntryDto>>

    @GET("api/diary/range")
    suspend fun getEntriesByDateRange(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): Response<List<DiaryEntryDto>>

    @POST("api/diary")
    suspend fun addEntry(@Body entry: DiaryEntryDto): Response<DiaryEntryDto>

    @PUT("api/diary/{id}")
    suspend fun updateEntry(
        @Path("id") id: Int,
        @Body entry: DiaryEntryDto
    ): Response<DiaryEntryDto>

    @DELETE("api/diary/{id}")
    suspend fun deleteEntry(@Path("id") id: Int): Response<Unit>
}
