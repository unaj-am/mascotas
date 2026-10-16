package com.app.core.domain.usecase.auth

import com.app.core.domain.repository.AuthRepository

class SignInUseCase(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<String> {

        return authRepository.signIn(
            email = email,
            password = password
        )
    }
}