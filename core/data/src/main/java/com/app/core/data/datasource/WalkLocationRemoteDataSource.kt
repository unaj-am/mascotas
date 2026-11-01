package com.app.core.data.datasource

import com.app.core.domain.model.WalkLocation
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalkLocationRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private fun currentLocationDocument(walkId: String) =
        firestore.collection("walks")
            .document(walkId)
            .collection("tracking")
            .document("current")

    private fun locationsCollection(walkId: String) =
        firestore.collection("walks")
            .document(walkId)
            .collection("locations")

    /**
     * Guarda la última ubicación y agrega el registro al historial.
     */
    suspend fun publishLocation(
        walkId: String,
        location: WalkLocation
    ) {
        require(walkId.isNotBlank()) {
            "El identificador del paseo es obligatorio"
        }

        val locationData = location.toFirestoreMap()
        val batch = firestore.batch()

        val currentDocument = currentLocationDocument(walkId)
        val historyDocument = locationsCollection(walkId).document()

        batch.set(currentDocument, locationData)
        batch.set(historyDocument, locationData)

        batch.commit().await()
    }

    /**
     * Recupera la última ubicación registrada.
     */
    suspend fun getLastLocation(
        walkId: String
    ): WalkLocation? {
        require(walkId.isNotBlank()) {
            "El identificador del paseo es obligatorio"
        }

        val snapshot = currentLocationDocument(walkId)
            .get()
            .await()

        return if (snapshot.exists()) {
            snapshot.toWalkLocation()
        } else {
            null
        }
    }

    /**
     * Escucha los cambios de la última ubicación en tiempo real.
     */
    fun observeCurrentLocation(
        walkId: String
    ): Flow<WalkLocation?> = callbackFlow {
        require(walkId.isNotBlank()) {
            "El identificador del paseo es obligatorio"
        }

        val listener = currentLocationDocument(walkId)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val location = if (snapshot?.exists() == true) {
                    runCatching {
                        snapshot.toWalkLocation()
                    }.getOrElse { exception ->
                        close(exception)
                        return@addSnapshotListener
                    }
                } else {
                    null
                }

                trySend(location)
            }

        awaitClose {
            listener.remove()
        }
    }

    /**
     * Escucha el historial completo de ubicaciones del paseo.
     */
    fun observeLocationHistory(
        walkId: String
    ): Flow<List<WalkLocation>> = callbackFlow {
        require(walkId.isNotBlank()) {
            "El identificador del paseo es obligatorio"
        }

        val listener = locationsCollection(walkId)
            .orderBy("recordedAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val locations = runCatching {
                    snapshot?.documents
                        ?.map { it.toWalkLocation() }
                        .orEmpty()
                }.getOrElse { exception ->
                    close(exception)
                    return@addSnapshotListener
                }

                trySend(locations)
            }

        awaitClose {
            listener.remove()
        }
    }

    private fun WalkLocation.toFirestoreMap(): Map<String, Any?> =
        mapOf(
            "latitude" to latitude,
            "longitude" to longitude,
            "accuracy" to accuracy.toDouble(),
            "bearing" to bearing?.toDouble(),
            "speed" to speed?.toDouble(),
            "recordedAt" to recordedAt
        )

    private fun DocumentSnapshot.toWalkLocation(): WalkLocation {
        val latitude = getDouble("latitude")
            ?: throw IllegalStateException(
                "La ubicación no contiene latitud"
            )

        val longitude = getDouble("longitude")
            ?: throw IllegalStateException(
                "La ubicación no contiene longitud"
            )

        val accuracy = getDouble("accuracy")
            ?: throw IllegalStateException(
                "La ubicación no contiene precisión"
            )

        val recordedAt = getLong("recordedAt")
            ?: throw IllegalStateException(
                "La ubicación no contiene fecha de registro"
            )

        return WalkLocation(
            latitude = latitude,
            longitude = longitude,
            accuracy = accuracy.toFloat(),
            bearing = getDouble("bearing")?.toFloat(),
            speed = getDouble("speed")?.toFloat(),
            recordedAt = recordedAt
        )
    }
}