package com.app.core.domain.usecase

import com.app.core.domain.model.WalkRequest
import com.app.core.domain.repository.WalkRequestRepository

class GetWalkRequestByIdUseCase(
    private val walkRequestRepository: WalkRequestRepository
) {
    suspend operator fun invoke(requestId: String): WalkRequest? {
        require(requestId.isNotBlank()) {
            "El identificador de la solicitud no puede estar vacío"
        }

        return walkRequestRepository.getRequestById(requestId)
    }
}