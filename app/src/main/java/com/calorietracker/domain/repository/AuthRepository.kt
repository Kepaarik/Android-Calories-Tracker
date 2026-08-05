package com.calorietracker.domain.repository

import com.calorietracker.domain.model.User
import com.calorietracker.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(email: String, password: String, username: String): Result<User>
    suspend fun telegramAuth(initData: String): Result<User>
    suspend fun logout(): Result<Unit>
    fun getCurrentUser(): Flow<User?>
    suspend fun getToken(): String?
    suspend fun saveToken(token: String)
}
