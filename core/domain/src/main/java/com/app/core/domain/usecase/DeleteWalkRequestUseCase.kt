package com.app.core.domain.usecase

import com.app.core.domain.repository.WalkRequestRepository

class DeleteWalkRequestUseCase(
    private val walkRequestRepository: WalkRequestRepository
) {
    suspend operator fun invoke(requestId: String) {
        require(requestId.isNotBlank()) {
            "El identificador de la solicitud no puede estar vacío"
        }

        val request = walkRequestRepository.getRequestById(requestId)

        require(request != null) {
            "No existe una solicitud con ese identificador"
        }

        require(request.status == com.app.core.domain.model.RequestStatus.PENDING) {
            "Solo se pueden eliminar solicitudes pendientes"
        }

        walkRequestRepository.deleteRequest(requestId)
    }
}