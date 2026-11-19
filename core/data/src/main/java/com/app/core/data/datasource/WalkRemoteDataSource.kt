
package com.app.core.data.datasource

import com.app.core.data.mapper.toFirestoreMapWithDefaults
import com.app.core.data.mapper.toWalk
import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkStatus
import com.app.core.domain.model.validateStatusChange
import java.time.Instant
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

            transaction.set(walkRef, walk.toFirestoreMapWithDefaults())
        }.await()
    }

    suspend fun updateWalkStatus(
        walkId: String,
        userId: String,
        status: WalkStatus
    ) {
        val walkRef = walksCollection.document(walkId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(walkRef)

            require(snapshot.exists()) {
                "No existe el paseo"
            }

            val walk = snapshot.toWalk()
            walk.validateStatusChange(userId, status)

            val timestampField = when (status) {
                WalkStatus.IN_PROGRESS -> "startedAt"
                WalkStatus.COMPLETED -> "finishedAt"
                WalkStatus.CANCELLED -> "cancelledAt"
                WalkStatus.SCHEDULED -> error("No se puede volver al estado programado")
            }

            transaction.update(
                walkRef,
                mapOf(
                    "status" to status.name,
                    timestampField to Instant.now().toString()
                )
            )
        }.await()
    }

}
