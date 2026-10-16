package com.app.core.domain.usecase.auth

import com.app.core.domain.repository.AuthRepository

import javax.inject.Inject

class SignInUseCase @Inject constructor(
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