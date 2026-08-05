package com.calorietracker.data.remote.api

import com.calorietracker.data.remote.dto.LoginRequestDto
import com.calorietracker.data.remote.dto.RegisterRequestDto
import com.calorietracker.data.remote.dto.TelegramAuthRequestDto
import com.calorietracker.data.remote.dto.UserResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<UserResponseDto>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequestDto): Response<UserResponseDto>

    @POST("api/auth/telegram")
    suspend fun telegramAuth(@Body request: TelegramAuthRequestDto): Response<UserResponseDto>

    @POST("api/auth/logout")
    suspend fun logout(): Response<Unit>
}
