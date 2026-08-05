package com.calorietracker.domain.repository

import com.calorietracker.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    suspend fun getProfile(): Result<UserProfile>
    suspend fun updateProfile(profile: UserProfile): Result<UserProfile>
    suspend fun deleteAccount(): Result<Unit>
    fun getProfileFlow(): Flow<UserProfile?>
}
