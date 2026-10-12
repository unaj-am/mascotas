package com.app.core.domain.usecase

import com.app.core.domain.model.WalkStatus
import com.app.core.domain.repository.WalkRepository

class CancelWalkUseCase(
    private val walkRepository: WalkRepository
) {
    suspend operator fun invoke(walkId: String) {
        require(walkId.isNotBlank()) {
            "El identificador del paseo no puede estar vacío"
        }

        val walk = walkRepository.getWalkById(walkId)

        require(walk != null) {
            "No existe un paseo con ese identificador"
        }

        require(walk.status == WalkStatus.SCHEDULED) {
            "Solo se pueden cancelar paseos programados"
        }

        walkRepository.updateWalkStatus(
            walkId,
            WalkStatus.CANCELLED
        )
    }
}