
package com.app.core.data.remote.walk

import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkStatus
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalkRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val walksCollection
        get() = firestore.collection("walks")

    suspend fun getWalkById(walkId: String): Walk? {
        val document = walksCollection
            .document(walkId)
            .get()
            .await()

        return if (document.exists()) {
            document.toWalk()
        } else {
            null
        }
    }

    suspend fun getWalksByOwner(ownerId: String): List<Walk> {
        val snapshot = walksCollection
            .whereEqualTo("ownerId", ownerId)
            .get()
            .await()

        return snapshot.documents.map { it.toWalk() }
    }

    suspend fun getWalksByWalker(walkerId: String): List<Walk> {
        val snapshot = walksCollection
            .whereEqualTo("walkerId", walkerId)
            .get()
            .await()

        return snapshot.documents.map { it.toWalk() }
    }

    suspend fun saveWalk(walk: Walk) {
        val walkRef = walksCollection.document(walk.id)

        firestore.runTransaction { transaction ->
            val existing = transaction.get(walkRef)

            require(!existing.exists()) {
                "Ya existe un paseo con ese identificador"
            }

            transaction.set(walkRef, walk.toFirestoreMap())
        }.await()
    }

    suspend fun updateWalkStatus(
        walkId: String,
        buildUpdates: (Walk) -> Map<String, Any>
    ) {
        val walkRef = walksCollection.document(walkId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(walkRef)

            require(snapshot.exists()) {
                "No existe el paseo"
            }

            val walk = snapshot.toWalk()
            val updates = buildUpdates(walk)

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
