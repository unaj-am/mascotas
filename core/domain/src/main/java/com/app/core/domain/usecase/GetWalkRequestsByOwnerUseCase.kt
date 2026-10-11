package com.app.core.domain.usecase

import com.app.core.domain.model.WalkRequest
import com.app.core.domain.repository.WalkRequestRepository

class GetWalkRequestsByOwnerUseCase(
    private val walkRequestRepository: WalkRequestRepository
) {
    suspend operator fun invoke(ownerId: String): List<WalkRequest> {
        require(ownerId.isNotBlank()) {
            "El identificador del dueño no puede estar vacío"
        }

        return walkRequestRepository.getRequestsByOwner(ownerId)
    }
}