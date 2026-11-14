package com.app.core.domain.usecase

import com.app.core.domain.model.RequestStatus
import com.app.core.domain.model.WalkRequest
import com.app.core.domain.repository.WalkRequestRepository
import com.app.core.domain.repository.WalkerRepository

import javax.inject.Inject

class CreateWalkRequestUseCase @Inject constructor(
    private val walkRequestRepository: WalkRequestRepository,
    private val walkerRepository: WalkerRepository
) {
    suspend operator fun invoke(request: WalkRequest) {
        require(request.id.isNotBlank()) {
            "El identificador de la solicitud es obligatorio"
        }
        require(request.ownerId.isNotBlank()) {
            "El identificador del dueño es obligatorio"
        }
        require(request.walkerId.isNotBlank()) {
            "El identificador del paseador es obligatorio"
        }
        require(request.petIds.isNotEmpty()) {
            "La solicitud debe incluir al menos una mascota"
        }
        require(request.scheduledAt.isNotBlank()) {
            "La fecha y hora del paseo son obligatorias"
        }
        require(request.durationMinutes > 0) {
            "La duración del paseo debe ser mayor que cero"
        }
        require(request.status == RequestStatus.PENDING) {
            "Una nueva solicitud debe comenzar en estado PENDING"
        }

        val walker = walkerRepository.getWalkerById(request.walkerId)
        check(walker?.available == true) {
            "Este paseador ya no está disponible para recibir solicitudes."
        }

        walkRequestRepository.createRequest(request)
    }
}