
package com.app.core.data.datasource

import com.app.core.data.notifications.NotificationDto
import com.app.core.data.notifications.toDomain
import com.app.core.domain.model.AppNotification
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private fun notificationsCollection(userId: String) =
        firestore.collection("users")
            .document(userId)
            .collection("notifications")

    fun observeNotifications(
        userId: String
    ): Flow<List<AppNotification>> = callbackFlow {

        val listener = notificationsCollection(userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener(
                MetadataChanges.INCLUDE
            ) { snapshot, error ->

                if (error != null) {
                    android.util.Log.e(
                        "NotificationsRemoteDataSource",
                        "Error al escuchar notificaciones",
                        error
                    )
                    close(error)
                    return@addSnapshotListener
                }

                val notifications = snapshot?.documents
                    ?.mapNotNull { document ->
                        val dto = document.toObject(
                            NotificationDto::class.java
                        )?.copy(
                            isRead = document.getBoolean("isRead") ?: false
                        )

                        dto?.toDomain(document.id)
                    } ?: emptyList()

                trySend(notifications)
            }

        awaitClose {
            listener.remove()
        }
    }

    suspend fun markAsRead(
        userId: String,
        notificationId: String
    ) {
        notificationsCollection(userId)
            .document(notificationId)
            .update("isRead", true)
            .await()
    }
}
