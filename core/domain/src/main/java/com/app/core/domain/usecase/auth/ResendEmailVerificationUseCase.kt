
package com.app.core.domain.usecase.auth

import com.app.core.domain.repository.AuthRepository

class ResendEmailVerificationUseCase(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(): Result<Unit> {
        return authRepository.resendEmailVerification()
    }
}
