
package com.app.core.data.repository

import com.app.core.domain.model.Walker
import com.app.core.domain.repository.WalkerRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalkerRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : WalkerRepository {

    private val walkersCollection
        get() = firestore.collection("walkers")

    private fun requireCurrentUserId(): String {
        return firebaseAuth.currentUser?.uid
            ?: throw IllegalStateException(
                "Debés iniciar sesión para gestionar el perfil de paseador"
            )
    }

    override suspend fun getWalkers(): List<Walker> {
        val snapshot = walkersCollection
            .get()
            .await()

        return snapshot.documents.map { document ->
            document.toWalker()
        }
    }

    override suspend fun getWalkerById(
        userId: String
    ): Walker? {
        val currentUserId = requireCurrentUserId()

        require(userId == currentUserId) {
            "No podés consultar el perfil de otro paseador"
        }

        val document = walkersCollection
            .document(userId)
            .get()
            .await()

        if (!document.exists()) {
            return null
        }

        return document.toWalker()
    }

    override suspend fun saveWalker(
        walker: Walker
    ) {
        val currentUserId = requireCurrentUserId()

        require(walker.userId == currentUserId) {
            "No podés crear el perfil de otro paseador"
        }

        val document = walkersCollection
            .document(currentUserId)
            .get()
            .await()

        require(!document.exists()) {
            "Ya existe un perfil de paseador para este usuario"
        }

        walkersCollection
            .document(currentUserId)
            .set(walker.toFirestoreMap())
            .await()
    }

    override suspend fun updateWalker(
        walker: Walker
    ) {
        val currentUserId = requireCurrentUserId()

        require(walker.userId == currentUserId) {
            "No podés modificar el perfil de otro paseador"
        }

        val document = walkersCollection
            .document(currentUserId)
            .get()
            .await()

        require(document.exists()) {
            "No existe el perfil de paseador que querés modificar"
        }

        walkersCollection
            .document(currentUserId)
            .set(walker.toFirestoreMap())
            .await()
    }

    override suspend fun getAvailableWalkers(): List<Walker> {
        val snapshot = walkersCollection
            .whereEqualTo("available", true)
            .get()
            .await()

        return snapshot.documents.map { document ->
            document.toWalker()
        }
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
