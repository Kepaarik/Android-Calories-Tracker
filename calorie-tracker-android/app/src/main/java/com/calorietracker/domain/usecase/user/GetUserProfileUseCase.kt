package com.calorietracker.domain.usecase.user

import com.calorietracker.domain.model.User
import com.calorietracker.domain.repository.UserProfileRepository

class GetUserProfileUseCase(
    private val userProfileRepository: UserProfileRepository
) {
    suspend operator fun invoke(userId: Int): Result<User> {
        return userProfileRepository.getUserProfile(userId)
    }
}
