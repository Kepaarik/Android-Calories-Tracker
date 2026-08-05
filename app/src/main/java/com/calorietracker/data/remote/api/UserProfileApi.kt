package com.calorietracker.data.remote.api

import com.calorietracker.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface UserProfileApi {
    
    @GET("user/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<UserProfileDto>
    
    @PUT("user/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateUserProfileRequestDto
    ): Response<UserProfileDto>
    
    @DELETE("user/account")
    suspend fun deleteAccount(
        @Header("Authorization") token: String
    ): Response<Unit>
}
