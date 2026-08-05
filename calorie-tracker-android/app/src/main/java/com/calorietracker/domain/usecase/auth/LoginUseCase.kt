package com.calorietracker.domain.usecase.auth

import com.calorietracker.domain.model.User
import com.calorietracker.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return authRepository.login(email, password)
    }
}
