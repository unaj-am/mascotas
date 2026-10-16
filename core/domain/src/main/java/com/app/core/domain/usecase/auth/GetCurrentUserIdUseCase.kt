package com.app.core.domain.usecase.auth

import com.app.core.domain.repository.AuthRepository

class GetCurrentUserIdUseCase(
    private val authRepository: AuthRepository
) {

    operator fun invoke(): String? {
        return authRepository.getCurrentUserId()
    }
}