package com.calorietracker.data.repository

import com.calorietracker.data.local.DailyGoalDao
import com.calorietracker.data.mapper.UserMapper.toDomain
import com.calorietracker.data.mapper.UserMapper.toDto
import com.calorietracker.data.remote.NetworkResult
import com.calorietracker.data.remote.api.AuthApi
import com.calorietracker.data.remote.dto.LoginRequestDto
import com.calorietracker.data.remote.dto.RegisterRequestDto
import com.calorietracker.data.remote.dto.TelegramAuthRequestDto
import com.calorietracker.data.remote.safeApiCall
import com.calorietracker.domain.model.Gender
import com.calorietracker.domain.model.User
import com.calorietracker.domain.model.UserProfile
import com.calorietracker.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val dailyGoalDao: DailyGoalDao
) : AuthRepository {

    private val _currentUser = MutableStateFlow<User?>(null)
    private var authToken: String? = null

    override suspend fun login(email: String, password: String): Result<User> {
        return when (val result = safeApiCall {
            authApi.login(LoginRequestDto(email, password))
        }) {
            is NetworkResult.Success -> {
                val response = result.data
                authToken = response.token
                saveToken(response.token)
                val user = response.user.toDomain()
                _currentUser.value = user
                Result.success(user)
            }
            is NetworkResult.Error -> {
                Result.failure(Exception("Login failed: ${result.message}"))
            }
            is NetworkResult.Loading -> {
                Result.failure(Exception("Loading..."))
            }
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        username: String
    ): Result<User> {
        return when (val result = safeApiCall {
            authApi.register(RegisterRequestDto(email, password, username))
        }) {
            is NetworkResult.Success -> {
                val response = result.data
                authToken = response.token
                saveToken(response.token)
                val user = response.user.toDomain()
                _currentUser.value = user
                Result.success(user)
            }
            is NetworkResult.Error -> {
                Result.failure(Exception("Registration failed: ${result.message}"))
            }
            is NetworkResult.Loading -> {
                Result.failure(Exception("Loading..."))
            }
        }
    }

    override suspend fun telegramAuth(initData: String): Result<User> {
        // Parse Telegram init data and create request
        // This requires parsing the initData string from Telegram Web App
        return Result.failure(Exception("Telegram auth not fully implemented yet"))
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            val token = authToken ?: return Result.failure(Exception("No token"))
            authApi.logout("Bearer $token")
            authToken = null
            _currentUser.value = null
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): Flow<User?> = _currentUser.asStateFlow()

    override suspend fun getToken(): String? = authToken

    override suspend fun saveToken(token: String) {
        authToken = token
        // Also save to SharedPreferences for persistence
        // This should be done via DataStore in production
    }
}
