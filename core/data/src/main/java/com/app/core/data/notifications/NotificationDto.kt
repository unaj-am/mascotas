package com.app.core.data.notifications
import com.google.firebase.Timestamp
data class NotificationDto(
    val userId: String = "",
    val title: String = "",
    val body: String = "",
    val type: String = "",
    val referenceId: String = "",
    val isRead: Boolean = false,
    val createdAt: Timestamp? = null
)
fun NotificationDto.toDomain(
    id: String
): com.app.core.domain.model.AppNotification {
    return com.app.core.domain.model.AppNotification(
        id = id,
        userId = userId,
        title = title,
        body = body,
        type = type,
        referenceId = referenceId,
        isRead = isRead,
        createdAt = createdAt?.toDate()?.time ?: 0L
    )
}