package com.calorietracker.domain.usecase.auth

import com.calorietracker.domain.model.User
import com.calorietracker.domain.repository.AuthRepository

class RegisterUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(username: String, email: String, password: String): Result<User> {
        return authRepository.register(username, email, password)
    }
}
