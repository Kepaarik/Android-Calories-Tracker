package com.calorietracker.domain.usecase.auth

import com.calorietracker.domain.model.User
import com.calorietracker.domain.repository.AuthRepository

class TelegramAuthUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(initData: String): Result<User> {
        return authRepository.telegramAuth(initData)
    }
}
