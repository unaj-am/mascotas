package com.app.core.domain.usecase

import com.app.core.domain.repository.WalkerRepository

class UpdateWalkerAvailabilityUseCase(
    private val walkerRepository: WalkerRepository
) {
    suspend operator fun invoke(
        userId: String,
        available: Boolean
    ) {
        require(userId.isNotBlank()) {
            "El identificador del usuario no puede estar vacío"
        }

        val walker = walkerRepository.getWalkerById(userId)

        require(walker != null) {
            "No existe un perfil de paseador con ese identificador"
        }

        walkerRepository.updateWalker(
            walker.copy(available = available)
        )
    }
}