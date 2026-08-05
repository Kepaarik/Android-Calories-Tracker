package com.calorietracker.domain.usecase.auth

import com.calorietracker.domain.model.User
import com.calorietracker.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String, username: String): Result<User> {
        return authRepository.register(email, password, username)
    }
}
