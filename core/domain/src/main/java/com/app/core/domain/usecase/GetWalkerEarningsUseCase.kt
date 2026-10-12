package com.app.core.domain.usecase

import com.app.core.domain.model.WalkStatus
import com.app.core.domain.repository.WalkRepository
import com.app.core.domain.repository.WalkerRepository

class GetWalkerEarningsUseCase(
    private val walkerRepository: WalkerRepository,
    private val walkRepository: WalkRepository
) {
    suspend operator fun invoke(walkerId: String): Double {
        require(walkerId.isNotBlank()) {
            "El identificador del paseador no puede estar vacío"
        }

        val walker = walkerRepository.getWalkerById(walkerId)
            ?: throw IllegalArgumentException(
                "No existe un perfil para ese paseador"
            )

        require(walker.pricePerWalk >= 0.0) {
            "La tarifa por paseo no puede ser negativa"
        }

        val walks = walkRepository.getWalksByWalker(walkerId)

        val completedWalks = walks.filter { walk ->
            walk.status == WalkStatus.COMPLETED
        }

        return completedWalks.size * walker.pricePerWalk
    }
}