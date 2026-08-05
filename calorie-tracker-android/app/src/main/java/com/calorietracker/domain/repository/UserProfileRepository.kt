package com.calorietracker.domain.repository

import com.calorietracker.domain.model.User

interface UserProfileRepository {
    suspend fun getUserProfile(userId: Int): Result<User>
    suspend fun updateUserProfile(user: User): Result<User>
}
