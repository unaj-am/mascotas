
package com.app.core.data.remote.walker

import com.app.core.domain.model.Walker
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalkerRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val walkersCollection
        get() = firestore.collection("walkers")

    suspend fun getWalkers(): List<Walker> {
        val snapshot = walkersCollection
            .get()
            .await()

        return snapshot.documents.map { document ->
            document.toWalker()
        }
    }

    suspend fun getWalkerById(userId: String): Walker? {
        val document = walkersCollection
            .document(userId)
            .get()
            .await()

        return if (document.exists()) {
            document.toWalker()
        } else {
            null
        }
    }

    suspend fun saveWalker(walker: Walker) {
        walkersCollection
            .document(walker.userId)
            .set(walker.toFirestoreMap())
            .await()
    }

    suspend fun updateWalker(walker: Walker) {
        walkersCollection
            .document(walker.userId)
            .update(
                mapOf(
                    "description" to walker.description,
                    "experienceYears" to walker.experienceYears,
                    "serviceArea" to walker.serviceArea,
                    "available" to walker.available,
                    "pricePerWalk" to walker.pricePerWalk,
                    "maxPets" to walker.maxPets
                )
            )
            .await()
    }

    suspend fun getAvailableWalkers(): List<Walker> {
        val snapshot = walkersCollection
            .whereEqualTo("available", true)
            .get()
            .await()

        return snapshot.documents.map { document ->
            document.toWalker()
        }
    }

    suspend fun updateAvailability(
        userId: String,
        available: Boolean
    ) {
        walkersCollection
            .document(userId)
            .update("available", available)
            .await()
    }

    private fun DocumentSnapshot.toWalker(): Walker {
        return Walker(
            userId = id,
            description = getString("description").orEmpty(),
            experienceYears = getLong("experienceYears")?.toInt() ?: 0,
            serviceArea = getString("serviceArea").orEmpty(),
            available = getBoolean("available") ?: false,
            pricePerWalk = getDouble("pricePerWalk") ?: 0.0,
            maxPets = getLong("maxPets")?.toInt() ?: 1,
            rating = getDouble("rating")
        )
    }

    private fun Walker.toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "description" to description,
            "experienceYears" to experienceYears,
            "serviceArea" to serviceArea,
            "available" to available,
            "pricePerWalk" to pricePerWalk,
            "maxPets" to maxPets,
            "rating" to rating
        )
    }
}
