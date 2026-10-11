package com.app.core.domain.usecase

import com.app.core.domain.model.RequestStatus
import com.app.core.domain.repository.WalkRequestRepository

class UpdateWalkRequestStatusUseCase(
    private val walkRequestRepository: WalkRequestRepository
) {
    suspend operator fun invoke(
        requestId: String,
        newStatus: RequestStatus
    ) {
        require(requestId.isNotBlank()) {
            "El identificador de la solicitud no puede estar vacío"
        }

        require(
            newStatus == RequestStatus.ACCEPTED ||
                    newStatus == RequestStatus.REJECTED ||
                    newStatus == RequestStatus.CANCELLED
        ) {
            "El nuevo estado no es válido para esta operación"
        }

        val request = walkRequestRepository.getRequestById(requestId)

        require(request != null) {
            "No existe una solicitud con ese identificador"
        }

        require(request.status == RequestStatus.PENDING) {
            "Solo se pueden actualizar solicitudes pendientes"
        }

        walkRequestRepository.updateRequestStatus(requestId, newStatus)
    }
}