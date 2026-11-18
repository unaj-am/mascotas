
package com.app.core.data.datasource

import com.app.core.data.mapper.toFirestoreMap
import com.app.core.data.mapper.toWalk
import com.app.core.data.mapper.toWalkRequest
import com.app.core.domain.model.RequestStatus
import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalkRequestRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val requestsCollection
        get() = firestore.collection("walkRequests")

    private val walksCollection
        get() = firestore.collection("walks")

    private val usersCollection
        get() = firestore.collection("users")

    private val petsCollection
        get() = firestore.collection("pets")

    suspend fun getRequestById(requestId: String): WalkRequest? {
        val document = requestsCollection
            .document(requestId)
            .get()
            .await()

        return if (document.exists()) {
            document.toWalkRequest()
        } else {
            null
        }
    }

    suspend fun getRequestsByOwner(ownerId: String): List<WalkRequest> {
        val snapshot = requestsCollection
            .whereEqualTo("ownerId", ownerId)
            .get()
            .await()

        return snapshot.documents.map { it.toWalkRequest() }
    }

    suspend fun getRequestsByWalker(walkerId: String): List<WalkRequest> {
        val snapshot = requestsCollection
            .whereEqualTo("walkerId", walkerId)
            .get()
            .await()

        return snapshot.documents.map { it.toWalkRequest() }
    }

    suspend fun getOwnerName(ownerId: String): String {
        val ownerDocument = usersCollection
            .document(ownerId)
            .get()
            .await()

        require(ownerDocument.exists()) {
            "No se encontró el perfil del dueño"
        }

        val firstName = ownerDocument
            .getString("name")
            .orEmpty()
            .trim()

        val lastName = ownerDocument
            .getString("lastName")
            .orEmpty()
            .trim()

        return listOf(firstName, lastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")
    }

    suspend fun getOwnedPetName(
        petId: String,
        ownerId: String
    ): String {
        val petDocument = petsCollection
            .document(petId)
            .get()
            .await()

        require(petDocument.exists()) {
            "No se encontró una de las mascotas seleccionadas"
        }

        require(petDocument.getString("ownerId") == ownerId) {
            "Una de las mascotas seleccionadas no pertenece al dueño"
        }

        return petDocument.getString("name")
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException(
                "Una de las mascotas no tiene un nombre válido"
            )
    }

    suspend fun getOwnedPetPhoto(
        petId: String,
        ownerId: String
    ): String {
        val petDocument = petsCollection
            .document(petId)
            .get()
            .await()

        require(petDocument.exists()) {
            "No se encontró una de las mascotas seleccionadas"
        }

        require(petDocument.getString("ownerId") == ownerId) {
            "Una de las mascotas seleccionadas no pertenece al dueño"
        }

        return petDocument.getString("photoUri").orEmpty()
    }

    suspend fun createRequest(
        request: WalkRequest,
        ownerName: String,
        petNames: List<String>
    ) {
        requestsCollection
            .document(request.id)
            .set(
                request.toFirestoreMap(
                    ownerName = ownerName,
                    petNames = petNames
                )
            )
            .await()
    }

    suspend fun updateRequestStatus(
        requestId: String,
        status: RequestStatus,
        validate: (WalkRequest) -> Unit
    ) {
        val requestRef = requestsCollection.document(requestId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(requestRef)

            require(snapshot.exists()) {
                "No existe la solicitud"
            }

            val request = snapshot.toWalkRequest()

            validate(request)

            val updates = mutableMapOf<String, Any>(
                "status" to status.name
            )

            when (status) {
                RequestStatus.REJECTED -> {
                    updates["rejectedAt"] =
                        FieldValue.serverTimestamp()
                }

                RequestStatus.CANCELLED -> {
                    updates["cancelledAt"] =
                        FieldValue.serverTimestamp()
                }

                else -> Unit
            }

            transaction.update(requestRef, updates)
        }.await()
    }

    suspend fun deleteRequest(
        requestId: String,
        validate: (WalkRequest) -> Unit
    ) {
        val requestRef = requestsCollection.document(requestId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(requestRef)

            if (!snapshot.exists()) {
                return@runTransaction
            }

            val request = snapshot.toWalkRequest()

            validate(request)

            transaction.delete(requestRef)
        }.await()
    }

    suspend fun acceptRequestAndCreateWalk(
        requestId: String,
        createWalk: (WalkRequest, Walk?) -> Walk?
    ): Walk {
        val requestRef = requestsCollection.document(requestId)
        val walkRef = walksCollection.document(requestId)

        return firestore.runTransaction { transaction ->
            val requestSnapshot = transaction.get(requestRef)
            val walkSnapshot = transaction.get(walkRef)

            require(requestSnapshot.exists()) {
                "No existe la solicitud"
            }

            val request = requestSnapshot.toWalkRequest()

            val existingWalk = if (walkSnapshot.exists()) {
                walkSnapshot.toWalk()
            } else {
                null
            }

            val newWalk = createWalk(request, existingWalk)

            if (newWalk == null) {
                return@runTransaction existingWalk
                    ?: throw IllegalStateException(
                        "No existe el paseo asociado a la solicitud"
                    )
            }

            transaction.set(
                walkRef,
                newWalk.toFirestoreMap()
            )

            transaction.update(
                requestRef,
                mapOf(
                    "status" to RequestStatus.ACCEPTED.name,
                    "acceptedAt" to FieldValue.serverTimestamp()
                )
            )

            newWalk
        }.await()
    }

}
