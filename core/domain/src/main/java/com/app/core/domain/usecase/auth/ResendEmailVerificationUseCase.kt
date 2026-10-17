package com.app.core.domain.usecase.auth

import com.app.core.domain.repository.AuthRepository
import javax.inject.Inject

class ResendEmailVerificationUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.resendEmailVerification()
    }
}