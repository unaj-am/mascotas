package com.app.core.domain.usecase.auth

import com.app.core.domain.repository.AuthRepository
import javax.inject.Inject

class IsEmailVerifiedUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Boolean> {
        return authRepository.isEmailVerified()
    }
}