package com.app.core.data.notifications

import com.app.core.domain.model.AppNotification
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

data class NotificationDto(
    val userId: String = "",
    val title: String = "",
    val body: String = "",
    val type: String = "",
    val referenceId: String = "",
    val isRead: Boolean = false,
    val createdAt: Timestamp? = null
)

fun NotificationDto.toDomain(id: String): AppNotification =
    AppNotification(
        id = id,
        userId = userId,
        title = title,
        body = body,
        type = type,
        referenceId = referenceId,
        isRead = isRead,
        createdAt = createdAt?.toDate()?.time ?: 0L
    )

fun DocumentSnapshot.toAppNotificationOrNull(): AppNotification? {
    val dto = toObject(NotificationDto::class.java)
        ?.copy(isRead = getBoolean("isRead") ?: false)
        ?: return null

    return dto.toDomain(id)
}
