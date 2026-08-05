package com.calorietracker.domain.repository

import com.calorietracker.domain.model.Gender
import com.calorietracker.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(username: String, email: String, password: String): Result<User>
    suspend fun telegramAuth(initData: String): Result<User>
    suspend fun logout()
    fun isLoggedIn(): Boolean
    fun getCurrentUser(): User?
}
