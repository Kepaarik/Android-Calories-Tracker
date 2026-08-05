package com.calorietracker.domain.usecase.user

import com.calorietracker.domain.model.User
import com.calorietracker.domain.repository.UserProfileRepository

class UpdateUserProfileUseCase(
    private val userProfileRepository: UserProfileRepository
) {
    suspend operator fun invoke(user: User): Result<User> {
        return userProfileRepository.updateUserProfile(user)
    }
}
