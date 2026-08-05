package com.calorietracker.data.repository

import com.calorietracker.data.local.dao.UserDao
import com.calorietracker.data.mapper.UserMapper
import com.calorietracker.data.remote.api.AuthApi
import com.calorietracker.data.remote.dto.LoginRequestDto
import com.calorietracker.data.remote.dto.RegisterRequestDto
import com.calorietracker.domain.model.Gender
import com.calorietracker.domain.model.User
import com.calorietracker.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val userDao: UserDao
) : AuthRepository {
    
    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = authApi.login(LoginRequestDto(email, password))
            if (response.isSuccessful && response.body() != null) {
                val userDto = response.body()!!
                // TODO: Сохранить токен в DataStore
                // TODO: Сохранить пользователя в локальную БД
                Result.success(userDto.toDomain())
            } else {
                Result.failure(Exception("Login failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun register(username: String, email: String, password: String): Result<User> {
        return try {
            val response = authApi.register(RegisterRequestDto(username, email, password))
            if (response.isSuccessful && response.body() != null) {
                val userDto = response.body()!!
                // TODO: Сохранить токен в DataStore
                // TODO: Сохранить пользователя в локальную БД
                Result.success(userDto.toDomain())
            } else {
                Result.failure(Exception("Registration failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun telegramAuth(initData: String): Result<User> {
        return try {
            val response = authApi.telegramAuth(initData)
            if (response.isSuccessful && response.body() != null) {
                val userDto = response.body()!!
                // TODO: Сохранить токен в DataStore
                // TODO: Сохранить пользователя в локальную БД
                Result.success(userDto.toDomain())
            } else {
                Result.failure(Exception("Telegram auth failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun logout() {
        // TODO: Очистить токен в DataStore
        // TODO: Очистить локальную БД
    }
    
    override fun isLoggedIn(): Boolean {
        // TODO: Проверить наличие токена в DataStore
        return false
    }
    
    override fun getCurrentUser(): User? {
        // TODO: Получить текущего пользователя из локальной БД
        return null
    }
}

// Extension function для маппинга DTO в Domain модель
private fun com.calorietracker.data.remote.dto.UserResponseDto.toDomain(): User {
    return User(
        id = id,
        username = username,
        email = email,
        age = age,
        gender = gender?.let { Gender.valueOf(it) },
        heightCm = heightCm,
        weightKg = weightKg,
        activityLevel = activityLevel?.let { com.calorietracker.domain.model.ActivityLevel.valueOf(it) },
        createdAt = createdAt
    )
}
