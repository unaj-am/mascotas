
package com.app.core.data.repository

import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkStatus
import com.app.core.domain.repository.WalkRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalkRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : WalkRepository {

    private val walksCollection
        get() = firestore.collection("walks")

    private fun requireCurrentUserId(): String =
        firebaseAuth.currentUser?.uid
            ?: throw IllegalStateException(
                "Debés iniciar sesión para gestionar paseos"
            )

    override suspend fun getWalkById(
        walkId: String
    ): Walk? {
        require(walkId.isNotBlank()) {
            "El identificador del paseo es obligatorio"
        }

        val currentUserId = requireCurrentUserId()

        val document = walksCollection
            .document(walkId)
            .get()
            .await()

        if (!document.exists()) return null

        val walk = document.toWalk()

        require(
            walk.ownerId == currentUserId ||
                    walk.walkerId == currentUserId
        ) {
            "No tenés permiso para consultar este paseo"
        }

        return walk
    }

    override suspend fun getWalksByOwner(
        ownerId: String
    ): List<Walk> {
        require(ownerId == requireCurrentUserId()) {
            "No podés consultar los paseos de otro dueño"
        }

        val snapshot = walksCollection
            .whereEqualTo("ownerId", ownerId)
            .get()
            .await()

        return snapshot.documents.map { it.toWalk() }
    }

    override suspend fun getWalksByWalker(
        walkerId: String
    ): List<Walk> {
        require(walkerId == requireCurrentUserId()) {
            "No podés consultar los paseos de otro paseador"
        }

        val snapshot = walksCollection
            .whereEqualTo("walkerId", walkerId)
            .get()
            .await()

        return snapshot.documents.map { it.toWalk() }
    }

    override suspend fun saveWalk(
        walk: Walk
    ) {
        require(walk.id.isNotBlank()) {
            "El identificador del paseo es obligatorio"
        }

        val currentUserId = requireCurrentUserId()

        require(
            walk.ownerId == currentUserId ||
                    walk.walkerId == currentUserId
        ) {
            "No podés guardar un paseo en el que no participás"
        }

        val walkRef = walksCollection.document(walk.id)

        firestore.runTransaction { transaction ->
            val existing = transaction.get(walkRef)

            require(!existing.exists()) {
                "Ya existe un paseo con ese identificador"
            }

            transaction.set(walkRef, walk.toFirestoreMap())
        }.await()
    }

    override suspend fun updateWalkStatus(
        walkId: String,
        status: WalkStatus
    ) {
        require(walkId.isNotBlank()) {
            "El identificador del paseo es obligatorio"
        }

        val currentUserId = requireCurrentUserId()
        val walkRef = walksCollection.document(walkId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(walkRef)

            require(snapshot.exists()) {
                "No existe el paseo"
            }

            val walk = snapshot.toWalk()

            require(
                walk.ownerId == currentUserId ||
                        walk.walkerId == currentUserId
            ) {
                "No tenés permiso para modificar este paseo"
            }

            when (status) {
                WalkStatus.IN_PROGRESS -> {
                    require(walk.walkerId == currentUserId) {
                        "Solo el paseador puede iniciar el paseo"
                    }
                    require(walk.status == WalkStatus.SCHEDULED) {
                        "Solo se pueden iniciar paseos programados"
                    }
                }

                WalkStatus.COMPLETED -> {
                    require(walk.walkerId == currentUserId) {
                        "Solo el paseador puede finalizar el paseo"
                    }
                    require(walk.status == WalkStatus.IN_PROGRESS) {
                        "Solo se pueden finalizar paseos en curso"
                    }
                }

                WalkStatus.CANCELLED -> {
                    require(walk.status == WalkStatus.SCHEDULED) {
                        "Solo se pueden cancelar paseos programados"
                    }
                }

                WalkStatus.SCHEDULED -> {
                    throw IllegalArgumentException(
                        "No se puede volver al estado programado"
                    )
                }
            }

            val updates = mutableMapOf<String, Any>(
                "status" to status.name
            )

            when (status) {
                WalkStatus.IN_PROGRESS -> {
                    updates["startedAt"] = Instant.now().toString()
                }

                WalkStatus.COMPLETED -> {
                    updates["finishedAt"] = Instant.now().toString()
                }

                WalkStatus.CANCELLED,
                WalkStatus.SCHEDULED -> Unit
            }

            transaction.update(walkRef, updates)
        }.await()
    }

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
