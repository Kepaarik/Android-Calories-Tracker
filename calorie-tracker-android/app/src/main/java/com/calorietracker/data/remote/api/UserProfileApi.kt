package com.calorietracker.data.remote.api

import com.calorietracker.data.remote.dto.UserResponseDto
import retrofit2.Response
import retrofit2.http.*

interface UserProfileApi {

    @GET("api/users/{id}")
    suspend fun getUserProfile(@Path("id") userId: Int): Response<UserResponseDto>

    @PUT("api/users/{id}")
    suspend fun updateUserProfile(
        @Path("id") userId: Int,
        @Body user: UserResponseDto
    ): Response<UserResponseDto>
}
