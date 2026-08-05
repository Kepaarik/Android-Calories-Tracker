package com.calorietracker.domain.usecase.user

import com.calorietracker.domain.model.UserProfile
import com.calorietracker.domain.repository.UserProfileRepository
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) {
    suspend operator fun invoke(profile: UserProfile): Result<UserProfile> {
        return userProfileRepository.updateProfile(profile)
    }
}
