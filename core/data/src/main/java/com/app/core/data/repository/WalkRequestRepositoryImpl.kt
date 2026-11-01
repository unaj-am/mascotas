
package com.app.core.data.repository

import com.app.core.data.remote.walkrequest.WalkRequestRemoteDataSource
import com.app.core.domain.model.RequestStatus
import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkRequest
import com.app.core.domain.model.WalkStatus
import com.app.core.domain.repository.WalkRequestRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalkRequestRepositoryImpl @Inject constructor(
    private val walkRequestRemoteDataSource: WalkRequestRemoteDataSource,
    private val firebaseAuth: FirebaseAuth
) : WalkRequestRepository {

    private fun requireCurrentUserId(): String =
        firebaseAuth.currentUser?.uid
            ?: throw IllegalStateException(
                "Debés iniciar sesión para gestionar solicitudes"
            )

    override suspend fun getRequestById(
        requestId: String
    ): WalkRequest? {
        require(requestId.isNotBlank()) {
            "El identificador de la solicitud es obligatorio"
        }

        val currentUserId = requireCurrentUserId()

        val request = walkRequestRemoteDataSource
            .getRequestById(requestId)
            ?: return null

        require(
            request.ownerId == currentUserId ||
                    request.walkerId == currentUserId
        ) {
            "No tenés permiso para consultar esta solicitud"
        }

        return request
    }

    override suspend fun getRequestsByOwner(
        ownerId: String
    ): List<WalkRequest> {
        require(ownerId == requireCurrentUserId()) {
            "No podés consultar solicitudes de otro dueño"
        }

        return walkRequestRemoteDataSource.getRequestsByOwner(ownerId)
    }

    override suspend fun getRequestsByWalker(
        walkerId: String
    ): List<WalkRequest> {
        require(walkerId == requireCurrentUserId()) {
            "No podés consultar solicitudes de otro paseador"
        }

        return walkRequestRemoteDataSource.getRequestsByWalker(walkerId)
    }

    override suspend fun createRequest(
        request: WalkRequest
    ) {
        require(request.id.isNotBlank()) {
            "El identificador de la solicitud es obligatorio"
        }

        require(request.ownerId == requireCurrentUserId()) {
            "Solo el dueño autenticado puede crear su solicitud"
        }

        require(request.petIds.isNotEmpty()) {
            "La solicitud debe incluir al menos una mascota"
        }

        require(request.petIds.size <= 5) {
            "Una solicitud puede incluir hasta cinco mascotas"
        }

        require(request.scheduledAt.isNotBlank()) {
            "La fecha del paseo es obligatoria"
        }

        require(request.durationMinutes > 0) {
            "La duración debe ser mayor que cero"
        }

        require(request.status == RequestStatus.PENDING) {
            "Una nueva solicitud debe estar pendiente"
        }

        val ownerName = walkRequestRemoteDataSource
            .getOwnerName(request.ownerId)

        require(ownerName.isNotBlank()) {
            "El perfil del dueño no tiene un nombre válido"
        }


        val petNames = request.petIds.map { petId ->
            walkRequestRemoteDataSource.getOwnedPetName(
                petId = petId,
                ownerId = request.ownerId
            )
        }

        val petPhotos = request.petIds.map { petId ->
            walkRequestRemoteDataSource.getOwnedPetPhoto(
                petId = petId,
                ownerId = request.ownerId
            )
        }

        walkRequestRemoteDataSource.createRequest(
            request = request.copy(petPhotos = petPhotos),
            ownerName = ownerName,
            petNames = petNames
        )

    }

    override suspend fun updateRequestStatus(
        requestId: String,
        status: RequestStatus
    ) {
        require(requestId.isNotBlank()) {
            "El identificador de la solicitud es obligatorio"
        }

        val currentUserId = requireCurrentUserId()

        walkRequestRemoteDataSource.updateRequestStatus(
            requestId = requestId,
            status = status
        ) { request ->

            require(request.status == RequestStatus.PENDING) {
                "Solo se pueden modificar solicitudes pendientes"
            }

            when (status) {
                RequestStatus.ACCEPTED,
                RequestStatus.REJECTED -> {
                    require(request.walkerId == currentUserId) {
                        "Solo el paseador destinatario puede aceptar o rechazar"
                    }
                }

                RequestStatus.CANCELLED -> {
                    require(request.ownerId == currentUserId) {
                        "Solo el dueño puede cancelar la solicitud"
                    }
                }

                RequestStatus.PENDING -> {
                    throw IllegalArgumentException(
                        "El estado nuevo no puede ser PENDING"
                    )
                }
            }
        }
    }

    override suspend fun deleteRequest(
        requestId: String
    ) {
        require(requestId.isNotBlank()) {
            "El identificador de la solicitud es obligatorio"
        }

        val currentUserId = requireCurrentUserId()

        walkRequestRemoteDataSource.deleteRequest(
            requestId = requestId
        ) { request ->

            require(request.ownerId == currentUserId) {
                "Solo el dueño puede eliminar la solicitud"
            }

            require(request.status == RequestStatus.PENDING) {
                "Solo se pueden eliminar solicitudes pendientes"
            }
        }
    }

    override suspend fun acceptRequestAndCreateWalk(
        requestId: String
    ): Walk {
        require(requestId.isNotBlank()) {
            "El identificador de la solicitud es obligatorio"
        }

        val currentUserId = requireCurrentUserId()

        return walkRequestRemoteDataSource.acceptRequestAndCreateWalk(
            requestId = requestId
        ) { request, existingWalk ->

            require(request.walkerId == currentUserId) {
                "Solo el paseador destinatario puede aceptar la solicitud"
            }

            // Si ya fue aceptada, se devuelve el paseo existente.
            if (request.status == RequestStatus.ACCEPTED) {
                require(existingWalk != null) {
                    "La solicitud está aceptada, pero no existe su paseo"
                }

                return@acceptRequestAndCreateWalk null
            }

            require(request.status == RequestStatus.PENDING) {
                "Solo se pueden aceptar solicitudes pendientes"
            }

            require(existingWalk == null) {
                "Ya existe un paseo asociado a esta solicitud"
            }

            Walk(
                id = request.id,
                ownerId = request.ownerId,
                walkerId = request.walkerId,
                petIds = request.petIds,
                scheduledAt = request.scheduledAt,
                durationMinutes = request.durationMinutes,
                status = WalkStatus.SCHEDULED,
                observations = request.observations
            )
        }
    }
}
