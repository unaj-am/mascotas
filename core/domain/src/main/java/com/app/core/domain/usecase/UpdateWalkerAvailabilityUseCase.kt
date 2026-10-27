package com.app.core.domain.usecase

import com.app.core.domain.repository.WalkerRepository
import javax.inject.Inject

class UpdateWalkerAvailabilityUseCase @Inject constructor(
    private val walkerRepository: WalkerRepository
) {
    suspend operator fun invoke(
        userId: String,
        available: Boolean
    ) {
        require(userId.isNotBlank()) {
            "El identificador del usuario no puede estar vacío"
        }

        walkerRepository.updateAvailability(
            userId = userId,
            available = available
        )
    }
}