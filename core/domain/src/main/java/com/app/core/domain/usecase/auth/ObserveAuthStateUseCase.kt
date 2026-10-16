
package com.app.core.domain.usecase.auth

import com.app.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class ObserveAuthStateUseCase(
    private val authRepository: AuthRepository
) {

    operator fun invoke(): Flow<String?> {
        return authRepository.observeAuthState()
    }
}
