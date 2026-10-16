
package com.app.core.domain.usecase.auth

import com.app.core.domain.repository.AuthRepository

class IsEmailVerifiedUseCase(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(): Result<Boolean> {
        return authRepository.isEmailVerified()
    }
}
