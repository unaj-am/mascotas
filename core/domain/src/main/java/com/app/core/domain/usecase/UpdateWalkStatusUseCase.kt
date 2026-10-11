package com.app.core.domain.usecase

import com.app.core.domain.model.WalkStatus
import com.app.core.domain.repository.WalkRepository

class UpdateWalkStatusUseCase(
    private val walkRepository: WalkRepository
) {
    suspend operator fun invoke(
        walkId: String,
        newStatus: WalkStatus
    ) {
        require(walkId.isNotBlank()) {
            "El identificador del paseo no puede estar vacío"
        }

        walkRepository.updateWalkStatus(walkId, newStatus)
    }
}