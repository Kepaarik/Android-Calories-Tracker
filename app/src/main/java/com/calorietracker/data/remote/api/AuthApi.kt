package com.calorietracker.data.remote.api

import com.calorietracker.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<AuthResponseDto>
    
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequestDto): Response<AuthResponseDto>
    
    @POST("auth/telegram")
    suspend fun telegramAuth(@Body request: TelegramAuthRequestDto): Response<AuthResponseDto>
    
    @POST("auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<Unit>
}
