
package com.app.core.data.datasource

import android.util.Log
import com.app.core.data.notifications.NotificationDto
import com.app.core.data.notifications.toDomain
import com.app.core.domain.model.AppNotification
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val usersCollection
        get() = firestore.collection("users")

    private val requestsCollection
        get() = firestore.collection("walkRequests")

    private val walksCollection
        get() = firestore.collection("walks")

    private fun notificationsCollection(userId: String) =
        usersCollection
            .document(userId)
            .collection("notifications")

    fun observeNotifications(
        userId: String
    ): Flow<List<AppNotification>> {

        // Notificaciones que ya existían en la aplicación.
        val existingNotifications = observeExistingNotifications(userId)

        // Solicitudes donde el usuario es dueño.
        val ownerRequests = observeDocuments(
            collectionName = "walkRequests",
            field = "ownerId",
            userId = userId
        ).map { documents ->
            documents.flatMap { document ->
                requestNotificationsForOwner(document)
            }
        }

        // Solicitudes donde el usuario es paseador.
        val walkerRequests = observeDocuments(
            collectionName = "walkRequests",
            field = "walkerId",
            userId = userId
        ).map { documents ->
            documents.flatMap { document ->
                requestNotificationsForWalker(document)
            }
        }

        // Paseos donde el usuario es dueño.
        val ownerWalks = observeDocuments(
            collectionName = "walks",
            field = "ownerId",
            userId = userId
        ).map { documents ->
            documents.flatMap { document ->
                walkNotificationsForOwner(document)
            }
        }

        // Paseos donde el usuario es paseador.
        val walkerWalks = observeDocuments(
            collectionName = "walks",
            field = "walkerId",
            userId = userId
        ).map { documents ->
            documents.flatMap { document ->
                walkNotificationsForWalker(document)
            }
        }

        // Identificadores leídos, persistidos en Firestore.
        val readIds = observeReadNotificationIds(userId)

        val allNotifications = combine(
            listOf(
                existingNotifications,
                ownerRequests,
                walkerRequests,
                ownerWalks,
                walkerWalks
            )
        ) { notificationLists ->
            notificationLists
                .flatMap { it }
                .distinctBy { it.id }
        }

        return combine(
            allNotifications,
            readIds
        ) { notifications, readNotificationIds ->

            notifications
                .filter { notification ->
                    if (notification.id.startsWith("event_")) {
                        notification.id !in readNotificationIds
                    } else {
                        !notification.isRead
                    }
                }
                .sortedByDescending { it.createdAt }
        }
    }

    private fun observeExistingNotifications(
        userId: String
    ): Flow<List<AppNotification>> = callbackFlow {

        val listener = notificationsCollection(userId)
            .orderBy(
                "createdAt",
                Query.Direction.DESCENDING
            )
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    Log.e(
                        TAG,
                        "Error al escuchar notificaciones existentes",
                        error
                    )
                    close(error)
                    return@addSnapshotListener
                }

                val notifications = snapshot
                    ?.documents
                    ?.mapNotNull { document ->

                        val dto = document.toObject(
                            NotificationDto::class.java
                        )?.copy(
                            isRead = document.getBoolean("isRead") ?: false
                        )

                        dto?.toDomain(document.id)
                    }
                    ?: emptyList()

                trySend(notifications)
            }

        awaitClose {
            listener.remove()
        }
    }

    private fun observeDocuments(
        collectionName: String,
        field: String,
        userId: String
    ): Flow<List<DocumentSnapshot>> = callbackFlow {

        val listener = firestore
            .collection(collectionName)
            .whereEqualTo(field, userId)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    Log.e(
                        TAG,
                        "Error al escuchar $collectionName por $field",
                        error
                    )
                    close(error)
                    return@addSnapshotListener
                }

                trySend(snapshot?.documents ?: emptyList())
            }

        awaitClose {
            listener.remove()
        }
    }

    private fun observeReadNotificationIds(
        userId: String
    ): Flow<Set<String>> = callbackFlow {

        val listener = usersCollection
            .document(userId)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    Log.e(
                        TAG,
                        "Error al escuchar notificaciones leídas",
                        error
                    )
                    close(error)
                    return@addSnapshotListener
                }

                val ids = (snapshot?.get("readNotificationIds") as? List<*>)
                    ?.filterIsInstance<String>()
                    ?.toSet()
                    ?: emptySet()

                trySend(ids)
            }

        awaitClose {
            listener.remove()
        }
    }

    // =========================================================
    // NOTIFICACIONES PARA EL DUEÑO DESDE WALK REQUESTS
    // =========================================================

    private fun requestNotificationsForOwner(
        document: DocumentSnapshot
    ): List<AppNotification> {

        val status = document.getString("status").orEmpty()

        return when (status) {

            "ACCEPTED" -> listOfNotNull(
                buildNotification(
                    id = "event_request_accepted_${document.id}",
                    userId = document.getString("ownerId").orEmpty(),
                    title = "Paseo aceptado",
                    body = "Tu paseo fue aceptado.",
                    type = "WALK_ACCEPTED",
                    referenceId = document.id,
                    createdAt = document.timestampMillis("acceptedAt")
                )
            )

            "REJECTED" -> listOfNotNull(
                buildNotification(
                    id = "event_request_rejected_${document.id}",
                    userId = document.getString("ownerId").orEmpty(),
                    title = "Solicitud rechazada",
                    body = "Tu solicitud de paseo fue rechazada.",
                    type = "WALK_REJECTED",
                    referenceId = document.id,
                    createdAt = document.timestampMillis("rejectedAt")
                )
            )

            else -> emptyList()
        }
    }

    // =========================================================
    // NOTIFICACIONES PARA EL PASEADOR DESDE WALK REQUESTS
    // =========================================================

    private fun requestNotificationsForWalker(
        document: DocumentSnapshot
    ): List<AppNotification> {

        val notifications = mutableListOf<AppNotification>()

        // La creación se conserva aunque luego cambie el estado.
        buildNotification(
            id = "event_request_created_${document.id}",
            userId = document.getString("walkerId").orEmpty(),
            title = "Nueva solicitud",
            body = "Tenés una nueva solicitud de paseo.",
            type = "WALK_REQUEST_CREATED",
            referenceId = document.id,
            createdAt = document.timestampMillis("createdAt")
        )?.let(notifications::add)

        // Cancelación de una solicitud todavía pendiente.
        if (document.getString("status") == "CANCELLED") {
            buildNotification(
                id = "event_request_cancelled_${document.id}",
                userId = document.getString("walkerId").orEmpty(),
                title = "Solicitud cancelada",
                body = "El dueño canceló el paseo.",
                type = "WALK_CANCELLED",
                referenceId = document.id,
                createdAt = document.timestampMillis("cancelledAt")
            )?.let(notifications::add)
        }

        return notifications
    }

    // =========================================================
    // NOTIFICACIONES PARA EL DUEÑO DESDE WALKS
    // =========================================================

    private fun walkNotificationsForOwner(
        document: DocumentSnapshot
    ): List<AppNotification> {

        val notifications = mutableListOf<AppNotification>()

        val ownerId = document.getString("ownerId").orEmpty()

        // Se conserva cuando el paseo pasa a COMPLETED.
        buildNotification(
            id = "event_walk_started_${document.id}",
            userId = ownerId,
            title = "Paseo iniciado",
            body = "Tu mascota ya está de paseo.",
            type = "WALK_STARTED",
            referenceId = document.id,
            createdAt = document.instantStringMillis("startedAt")
        )?.let(notifications::add)

        buildNotification(
            id = "event_walk_completed_${document.id}",
            userId = ownerId,
            title = "Paseo finalizado",
            body = "Tu mascota terminó su paseo.",
            type = "WALK_COMPLETED",
            referenceId = document.id,
            createdAt = document.instantStringMillis("finishedAt")
        )?.let(notifications::add)

        return notifications
    }

    // =========================================================
    // NOTIFICACIONES PARA EL PASEADOR DESDE WALKS
    // =========================================================

    private fun walkNotificationsForWalker(
        document: DocumentSnapshot
    ): List<AppNotification> {

        if (document.getString("status") != "CANCELLED") {
            return emptyList()
        }

        // El modelo actual de Walk todavía no guarda cancelledAt.
        // Por eso usamos una fecha estable del documento si existe.
        val cancelledAt = document.timestampMillis("cancelledAt")
            ?: document.instantStringMillis("cancelledAt")

        return listOfNotNull(
            buildNotification(
                id = "event_walk_cancelled_${document.id}",
                userId = document.getString("walkerId").orEmpty(),
                title = "Paseo cancelado",
                body = "El dueño canceló el paseo.",
                type = "WALK_CANCELLED",
                referenceId = document.id,
                createdAt = cancelledAt
            )
        )
    }

    // =========================================================
    // CONSTRUCCIÓN DE NOTIFICACIONES
    // =========================================================

    private fun buildNotification(
        id: String,
        userId: String,
        title: String,
        body: String,
        type: String,
        referenceId: String,
        createdAt: Long?
    ): AppNotification? {

        if (userId.isBlank() || createdAt == null) {
            return null
        }

        return AppNotification(
            id = id,
            userId = userId,
            title = title,
            body = body,
            type = type,
            referenceId = referenceId,
            isRead = false,
            createdAt = createdAt
        )
    }

    private fun DocumentSnapshot.timestampMillis(
        field: String
    ): Long? =
        getTimestamp(field)?.toDate()?.time

    private fun DocumentSnapshot.instantStringMillis(
        field: String
    ): Long? {

        val value = getString(field)
            ?.takeIf { it.isNotBlank() }
            ?: return null

        return runCatching {
            Instant.parse(value).toEpochMilli()
        }.getOrNull()
    }

    // =========================================================
    // MARCAR COMO LEÍDA
    // =========================================================

    suspend fun markAsRead(
        userId: String,
        notificationId: String
    ) {

        if (notificationId.startsWith("event_")) {

            // Persistimos la lectura en el perfil del usuario.
            usersCollection
                .document(userId)
                .update(
                    "readNotificationIds",
                    FieldValue.arrayUnion(notificationId)
                )
                .await()

        } else {

            // Mantenemos el comportamiento de las
            // notificaciones anteriores.
            notificationsCollection(userId)
                .document(notificationId)
                .update("isRead", true)
                .await()
        }
    }

    companion object {
        private const val TAG = "NotificationRemoteDataSource"
    }
}
