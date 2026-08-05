package com.calorietracker.domain.usecase.user

import com.calorietracker.domain.model.UserProfile
import com.calorietracker.domain.repository.UserProfileRepository
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) {
    suspend operator fun invoke(): Result<UserProfile> {
        return userProfileRepository.getProfile()
    }
}
