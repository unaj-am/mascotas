package com.app.core.domain.usecase.auth

import com.app.core.domain.repository.AuthRepository

class SignOutUseCase(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke() {
        authRepository.signOut()
    }
}