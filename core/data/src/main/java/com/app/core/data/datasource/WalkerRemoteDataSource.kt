
package com.app.core.data.datasource

import com.app.core.data.mapper.toFirestoreMap
import com.app.core.data.mapper.toFirestoreUpdateMap
import com.app.core.data.mapper.toWalker
import com.app.core.domain.model.Walker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
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
            .get(Source.SERVER)
            .await()

        return snapshot.documents.map { document ->
            document.toWalker()
        }
    }

    suspend fun getWalkerById(userId: String): Walker? {

        val document = walkersCollection
            .document(userId)
            .get(Source.SERVER)
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
            .update(walker.toFirestoreUpdateMap())
            .await()
    }

    suspend fun getAvailableWalkers(): List<Walker> {

        val snapshot = walkersCollection
            .whereEqualTo("available", true)
            .get(Source.SERVER)
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

}
