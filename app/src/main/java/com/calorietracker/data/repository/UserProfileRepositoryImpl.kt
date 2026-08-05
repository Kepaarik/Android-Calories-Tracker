package com.calorietracker.data.repository

import com.calorietracker.data.mapper.UserMapper.toDomain
import com.calorietracker.data.remote.NetworkResult
import com.calorietracker.data.remote.api.UserProfileApi
import com.calorietracker.data.remote.safeApiCall
import com.calorietracker.domain.model.UserProfile
import com.calorietracker.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepositoryImpl @Inject constructor(
    private val userProfileApi: UserProfileApi
) : UserProfileRepository {

    private val _profile = MutableStateFlow<UserProfile?>(null)

    override suspend fun getProfile(): Result<UserProfile> {
        val token = getToken() ?: return Result.failure(Exception("Not authenticated"))
        
        return when (val result = safeApiCall {
            userProfileApi.getProfile("Bearer $token")
        }) {
            is NetworkResult.Success -> {
                val profile = result.data.toDomain()
                _profile.value = profile
                Result.success(profile)
            }
            is NetworkResult.Error -> {
                Result.failure(Exception("Failed to get profile: ${result.message}"))
            }
            is NetworkResult.Loading -> {
                Result.failure(Exception("Loading..."))
            }
        }
    }

    override suspend fun updateProfile(profile: UserProfile): Result<UserProfile> {
        val token = getToken() ?: return Result.failure(Exception("Not authenticated"))
        
        val request = com.calorietracker.data.remote.dto.UpdateUserProfileRequestDto(
            username = profile.username,
            gender = profile.gender?.let { com.calorietracker.data.mapper.UserMapper.toDto(it) },
            age = profile.age,
            heightCm = profile.heightCm,
            weightKg = profile.weightKg,
            activityLevel = profile.activityLevel?.let { com.calorietracker.data.mapper.UserMapper.toDto(it) },
            dailyCalorieGoal = profile.dailyCalorieGoal
        )
        
        return when (val result = safeApiCall {
            userProfileApi.updateProfile("Bearer $token", request)
        }) {
            is NetworkResult.Success -> {
                val updatedProfile = result.data.toDomain()
                _profile.value = updatedProfile
                Result.success(updatedProfile)
            }
            is NetworkResult.Error -> {
                Result.failure(Exception("Failed to update profile: ${result.message}"))
            }
            is NetworkResult.Loading -> {
                Result.failure(Exception("Loading..."))
            }
        }
    }

    override suspend fun deleteAccount(): Result<Unit> {
        val token = getToken() ?: return Result.failure(Exception("Not authenticated"))
        
        return when (val result = safeApiCall {
            userProfileApi.deleteAccount("Bearer $token")
        }) {
            is NetworkResult.Success -> {
                _profile.value = null
                Result.success(Unit)
            }
            is NetworkResult.Error -> {
                Result.failure(Exception("Failed to delete account: ${result.message}"))
            }
            is NetworkResult.Loading -> {
                Result.failure(Exception("Loading..."))
            }
        }
    }

    override fun getProfileFlow(): Flow<UserProfile?> = _profile.asStateFlow()

    private fun getToken(): String? {
        // This should be retrieved from DataStore or SharedPreferences
        return null
    }
}
