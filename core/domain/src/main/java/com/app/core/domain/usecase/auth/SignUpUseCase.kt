package com.app.core.domain.usecase.auth

import com.app.core.domain.repository.AuthRepository

class SignUpUseCase(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<String> {

        return authRepository.signUp(
            email = email,
            password = password
        )
    }
}