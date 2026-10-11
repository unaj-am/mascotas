package com.app.core.domain.usecase

import com.app.core.domain.model.Walk
import com.app.core.domain.repository.WalkRepository

class GetWalkByIdUseCase(
    private val walkRepository: WalkRepository
) {
    suspend operator fun invoke(walkId: String): Walk? {
        require(walkId.isNotBlank()) {
            "El identificador del paseo no puede estar vacío"
        }

        return walkRepository.getWalkById(walkId)
    }
}