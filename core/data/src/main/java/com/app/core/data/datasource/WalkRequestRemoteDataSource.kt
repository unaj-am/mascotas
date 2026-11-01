
package com.app.core.data.remote.walkrequest

import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkRequest
import com.app.core.domain.model.WalkStatus
import com.app.core.domain.model.RequestStatus
import com.google.firebase.firestore.DocumentSnapshot
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

        val firstName = ownerDocument.getString("name").orEmpty().trim()
        val lastName = ownerDocument.getString("lastName").orEmpty().trim()

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
    ){
        val requestRef = requestsCollection.document(requestId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(requestRef)

            require(snapshot.exists()) {
                "No existe la solicitud"
            }

            val request = snapshot.toWalkRequest()

            validate(request)

            transaction.update(requestRef, "status", status.name)
        }.await()
    }

    suspend fun deleteRequest(
        requestId: String,
        validate: (WalkRequest) -> Unit
    ) {
        val requestRef = requestsCollection.document(requestId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(requestRef)

            if (!snapshot.exists()) return@runTransaction

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

            transaction.set(walkRef, newWalk.toFirestoreMap())

            transaction.update(
                requestRef,
                "status",
                RequestStatus.ACCEPTED.name
            )

            newWalk
        }.await()
    }

    private fun WalkRequest.toFirestoreMap(
        ownerName: String,
        petNames: List<String>
    ): Map<String, Any> =
        mapOf(
            "ownerId" to ownerId,
            "ownerName" to ownerName,
            "walkerId" to walkerId,
            "petIds" to petIds,
            "petNames" to petNames,
            "petPhotos" to petPhotos,
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
            ownerName = getString("ownerName").orEmpty(),
            walkerId = getString("walkerId")
                ?: throw IllegalStateException("La solicitud no tiene paseador"),
            petIds = get("petIds") as? List<String> ?: emptyList(),
            petNames = get("petNames") as? List<String> ?: emptyList(),
            petPhotos = (get("petPhotos") as? List<*>)
                ?.map { it as? String ?: "" }
                ?: emptyList(),
            scheduledAt = getString("scheduledAt").orEmpty(),
            durationMinutes = getLong("durationMinutes")?.toInt() ?: 0,
            status = getString("status")?.let {
                runCatching { RequestStatus.valueOf(it) }.getOrNull()
            } ?: RequestStatus.PENDING,
            observations = getString("observations").orEmpty()
        )

    private fun Walk.toFirestoreMap(): Map<String, Any?> =
        mapOf(
            "ownerId" to ownerId,
            "walkerId" to walkerId,
            "petIds" to petIds,
            "scheduledAt" to scheduledAt,
            "durationMinutes" to durationMinutes,
            "status" to status.name,
            "startedAt" to startedAt,
            "finishedAt" to finishedAt,
            "distanceKm" to distanceKm,
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
