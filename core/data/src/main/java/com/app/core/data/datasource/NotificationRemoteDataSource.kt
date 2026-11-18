
package com.app.core.data.datasource

import android.util.Log
import com.app.core.data.notifications.toAppNotificationOrNull
import com.app.core.domain.model.AppNotification
import com.app.core.data.mapper.requestNotificationsForOwner
import com.app.core.data.mapper.requestNotificationsForWalker
import com.app.core.data.mapper.walkNotificationsForOwner
import com.app.core.data.mapper.walkNotificationsForWalker
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
                document.requestNotificationsForOwner()
            }
        }

        // Solicitudes donde el usuario es paseador.
        val walkerRequests = observeDocuments(
            collectionName = "walkRequests",
            field = "walkerId",
            userId = userId
        ).map { documents ->
            documents.flatMap { document ->
                document.requestNotificationsForWalker()
            }
        }

        // Paseos donde el usuario es dueño.
        val ownerWalks = observeDocuments(
            collectionName = "walks",
            field = "ownerId",
            userId = userId
        ).map { documents ->
            documents.flatMap { document ->
                document.walkNotificationsForOwner()
            }
        }

        // Paseos donde el usuario es paseador.
        val walkerWalks = observeDocuments(
            collectionName = "walks",
            field = "walkerId",
            userId = userId
        ).map { documents ->
            documents.flatMap { document ->
                document.walkNotificationsForWalker()
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

                        document.toAppNotificationOrNull()
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
