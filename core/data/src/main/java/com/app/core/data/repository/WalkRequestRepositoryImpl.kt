
package com.app.core.data.repository

import com.app.core.domain.model.RequestStatus
import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkRequest
import com.app.core.domain.model.WalkStatus
import com.app.core.domain.repository.WalkRequestRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalkRequestRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : WalkRequestRepository {

    private val requestsCollection
        get() = firestore.collection("walkRequests")

    private val walksCollection
        get() = firestore.collection("walks")

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

        val document = requestsCollection
            .document(requestId)
            .get()
            .await()

        if (!document.exists()) return null

        val request = document.toWalkRequest()

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

        val snapshot = requestsCollection
            .whereEqualTo("ownerId", ownerId)
            .get()
            .await()

        return snapshot.documents.map { it.toWalkRequest() }
    }

    override suspend fun getRequestsByWalker(
        walkerId: String
    ): List<WalkRequest> {
        require(walkerId == requireCurrentUserId()) {
            "No podés consultar solicitudes de otro paseador"
        }

        val snapshot = requestsCollection
            .whereEqualTo("walkerId", walkerId)
            .get()
            .await()

        return snapshot.documents.map { it.toWalkRequest() }
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
        require(request.scheduledAt.isNotBlank()) {
            "La fecha del paseo es obligatoria"
        }
        require(request.durationMinutes > 0) {
            "La duración debe ser mayor que cero"
        }
        require(request.status == RequestStatus.PENDING) {
            "Una nueva solicitud debe estar pendiente"
        }

        val requestRef = requestsCollection.document(request.id)

        firestore.runTransaction { transaction ->
            val existing = transaction.get(requestRef)

            require(!existing.exists()) {
                "Ya existe una solicitud con ese identificador"
            }

            transaction.set(requestRef, request.toFirestoreMap())
        }.await()
    }

    override suspend fun updateRequestStatus(
        requestId: String,
        status: RequestStatus
    ) {
        require(requestId.isNotBlank()) {
            "El identificador de la solicitud es obligatorio"
        }

        val currentUserId = requireCurrentUserId()
        val requestRef = requestsCollection.document(requestId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(requestRef)

            require(snapshot.exists()) {
                "No existe la solicitud"
            }

            val request = snapshot.toWalkRequest()

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

            transaction.update(requestRef, "status", status.name)
        }.await()
    }

    override suspend fun deleteRequest(
        requestId: String
    ) {
        require(requestId.isNotBlank()) {
            "El identificador de la solicitud es obligatorio"
        }

        val currentUserId = requireCurrentUserId()
        val requestRef = requestsCollection.document(requestId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(requestRef)

            if (!snapshot.exists()) return@runTransaction

            val request = snapshot.toWalkRequest()

            require(request.ownerId == currentUserId) {
                "Solo el dueño puede eliminar la solicitud"
            }
            require(request.status == RequestStatus.PENDING) {
                "Solo se pueden eliminar solicitudes pendientes"
            }

            transaction.delete(requestRef)
        }.await()
    }

    override suspend fun acceptRequestAndCreateWalk(
        requestId: String
    ): Walk {
        require(requestId.isNotBlank()) {
            "El identificador de la solicitud es obligatorio"
        }

        val currentUserId = requireCurrentUserId()
        val requestRef = requestsCollection.document(requestId)
        val walkRef = walksCollection.document(requestId)

        return firestore.runTransaction { transaction ->
            val requestSnapshot = transaction.get(requestRef)
            val walkSnapshot = transaction.get(walkRef)

            require(requestSnapshot.exists()) {
                "No existe la solicitud"
            }

            val request = requestSnapshot.toWalkRequest()

            require(request.walkerId == currentUserId) {
                "Solo el paseador destinatario puede aceptar la solicitud"
            }

            // Si ya se aceptó, devolver el paseo existente permite
            // reintentar sin crear otro documento.
            if (request.status == RequestStatus.ACCEPTED) {
                require(walkSnapshot.exists()) {
                    "La solicitud está aceptada, pero no existe su paseo"
                }
                return@runTransaction walkSnapshot.toWalk()
            }

            require(request.status == RequestStatus.PENDING) {
                "Solo se pueden aceptar solicitudes pendientes"
            }
            require(!walkSnapshot.exists()) {
                "Ya existe un paseo asociado a esta solicitud"
            }

            val walk = Walk(
                id = request.id,
                ownerId = request.ownerId,
                walkerId = request.walkerId,
                petIds = request.petIds,
                scheduledAt = request.scheduledAt,
                durationMinutes = request.durationMinutes,
                status = WalkStatus.SCHEDULED,
                observations = request.observations
            )

            transaction.set(walkRef, walk.toFirestoreMap())
            transaction.update(
                requestRef,
                "status",
                RequestStatus.ACCEPTED.name
            )

            walk
        }.await()
    }

    private fun WalkRequest.toFirestoreMap(): Map<String, Any> =
        mapOf(
            "ownerId" to ownerId,
            "walkerId" to walkerId,
            "petIds" to petIds,
            "scheduledAt" to scheduledAt,
            "durationMinutes" to durationMinutes,
            "status" to status.name,
            "observations" to observations
        )

    private fun DocumentSnapshot.toWalkRequest(): WalkRequest =
        WalkRequest(
            id = id,
            ownerId = getString("ownerId")
                ?: throw IllegalStateException("La solicitud no tiene dueño"),
            walkerId = getString("walkerId")
                ?: throw IllegalStateException("La solicitud no tiene paseador"),
            petIds = get("petIds") as? List<String> ?: emptyList(),
            scheduledAt = getString("scheduledAt").orEmpty(),
            durationMinutes = getLong("durationMinutes")?.toInt() ?: 0,
            status = getString("status")?.let {
                runCatching { RequestStatus.valueOf(it) }.getOrNull()
            } ?: RequestStatus.PENDING,
            observations = getString("observations").orEmpty()
        )

    private fun Walk.toFirestoreMap(): Map<String, Any> =
        mapOf(
            "ownerId" to ownerId,
            "walkerId" to walkerId,
            "petIds" to petIds,
            "scheduledAt" to scheduledAt,
            "durationMinutes" to durationMinutes,
            "status" to status.name,
            "startedAt" to (startedAt ?: ""),
            "finishedAt" to (finishedAt ?: ""),
            "distanceKm" to (distanceKm ?: 0.0),
            "observations" to observations
        )

    private fun DocumentSnapshot.toWalk(): Walk =
        Walk(
            id = id,
            ownerId = getString("ownerId")
                ?: throw IllegalStateException("El paseo no tiene dueño"),
            walkerId = getString("walkerId")
                ?: throw IllegalStateException("El paseo no tiene paseador"),
            petIds = get("petIds") as? List<String> ?: emptyList(),
            scheduledAt = getString("scheduledAt").orEmpty(),
            durationMinutes = getLong("durationMinutes")?.toInt() ?: 0,
            status = getString("status")?.let {
                runCatching { WalkStatus.valueOf(it) }.getOrNull()
            } ?: WalkStatus.SCHEDULED,
            startedAt = getString("startedAt")?.takeIf { it.isNotBlank() },
            finishedAt = getString("finishedAt")?.takeIf { it.isNotBlank() },
            distanceKm = getDouble("distanceKm")?.takeIf { it > 0.0 },
            observations = getString("observations").orEmpty()
        )
}
