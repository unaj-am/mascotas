package com.app.core.domain.usecase

import com.app.core.domain.model.Walk
import com.app.core.domain.repository.WalkRepository

class GetWalksByOwnerUseCase(
    private val walkRepository: WalkRepository
) {
    suspend operator fun invoke(ownerId: String): List<Walk> {
        require(ownerId.isNotBlank()) {
            "El identificador del dueño no puede estar vacío"
        }

        return walkRepository.getWalksByOwner(ownerId)
    }
}