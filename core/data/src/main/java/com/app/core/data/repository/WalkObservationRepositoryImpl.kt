package com.app.core.data.repository

import com.app.core.data.mapper.toWalk
import com.app.core.data.mapper.toWalkRequest
import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkRequest
import com.app.core.domain.repository.WalkObservationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Singleton
class WalkObservationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : WalkObservationRepository {

    private fun currentUserId(): String =
        firebaseAuth.currentUser?.uid
            ?: throw IllegalStateException("Debés iniciar sesión para consultar paseos")

    override fun observeWalk(walkId: String): Flow<Walk?> {
        require(walkId.isNotBlank()) { "El identificador del paseo es obligatorio" }
        val userId = currentUserId()
        return observeDocument(firestore.collection("walks").document(walkId))
            .map { document ->
                document?.toWalk()?.also { walk ->
                    require(walk.ownerId == userId || walk.walkerId == userId) {
                        "No tenés permiso para consultar este paseo"
                    }
                }
            }
            .distinctUntilChanged()
    }

    override fun observeWalksByOwner(ownerId: String): Flow<List<Walk>> =
        observeUserDocuments("walks", "ownerId", ownerId)
            .map { documents -> documents.map { it.toWalk() } }
            .distinctUntilChanged()

    override fun observeWalksByWalker(walkerId: String): Flow<List<Walk>> =
        observeUserDocuments("walks", "walkerId", walkerId)
            .map { documents -> documents.map { it.toWalk() } }
            .distinctUntilChanged()

    override fun observeRequestsByOwner(ownerId: String): Flow<List<WalkRequest>> =
        observeUserDocuments("walkRequests", "ownerId", ownerId)
            .map { documents -> documents.map { it.toWalkRequest() } }
            .distinctUntilChanged()

    override fun observeRequestsByWalker(walkerId: String): Flow<List<WalkRequest>> =
        observeUserDocuments("walkRequests", "walkerId", walkerId)
            .map { documents -> documents.map { it.toWalkRequest() } }
            .distinctUntilChanged()

    override fun observeRequest(requestId: String): Flow<WalkRequest?> {
        require(requestId.isNotBlank()) { "El identificador de la solicitud es obligatorio" }
        val userId = currentUserId()
        return observeDocument(firestore.collection("walkRequests").document(requestId))
            .map { document ->
                document?.toWalkRequest()?.also { request ->
                    require(request.ownerId == userId || request.walkerId == userId) {
                        "No tenés permiso para consultar esta solicitud"
                    }
                }
            }
            .distinctUntilChanged()
    }

    private fun observeUserDocuments(
        collection: String,
        field: String,
        userId: String
    ): Flow<List<DocumentSnapshot>> {
        require(userId.isNotBlank() && userId == currentUserId()) {
            "No podés consultar los datos de otro usuario"
        }
        return observeQuery(firestore.collection(collection).whereEqualTo(field, userId))
    }

    private fun observeQuery(query: Query): Flow<List<DocumentSnapshot>> = callbackFlow {
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
            } else if (snapshot != null) {
                trySend(snapshot.documents)
            }
        }
        awaitClose { listener.remove() }
    }

    private fun observeDocument(reference: DocumentReference): Flow<DocumentSnapshot?> =
        callbackFlow {
            val listener = reference.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                } else if (snapshot != null) {
                    trySend(snapshot.takeIf { it.exists() })
                }
            }
            awaitClose { listener.remove() }
        }
}
