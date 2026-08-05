package com.calorietracker.domain.usecase.auth

import com.calorietracker.domain.model.User
import com.calorietracker.domain.repository.AuthRepository
import javax.inject.Inject

class TelegramAuthUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(initData: String): Result<User> {
        return authRepository.telegramAuth(initData)
    }
}
