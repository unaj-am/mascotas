package com.app.core.domain.usecase

import com.app.core.domain.model.WalkRequest
import com.app.core.domain.repository.WalkRequestRepository

class GetWalkRequestsByWalkerUseCase(
    private val walkRequestRepository: WalkRequestRepository
) {
    suspend operator fun invoke(walkerId: String): List<WalkRequest> {
        require(walkerId.isNotBlank()) {
            "El identificador del paseador no puede estar vacío"
        }

        return walkRequestRepository.getRequestsByWalker(walkerId)
    }
}