package com.app.core.domain.model

object NotificationType {
    const val WALK_ACCEPTED = "WALK_ACCEPTED"
    const val WALK_REJECTED = "WALK_REJECTED"
    const val WALK_REQUEST_CREATED = "WALK_REQUEST_CREATED"
    const val WALK_REQUEST_CANCELLED = "WALK_REQUEST_CANCELLED"
    const val WALK_STARTED = "WALK_STARTED"
    const val WALK_COMPLETED = "WALK_COMPLETED"
    const val WALK_CANCELLED = "WALK_CANCELLED"
}

data class AppNotification(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val body: String = "",
    val type: String = "",
    val referenceId: String = "",
    val isRead: Boolean = false,
    val createdAt: Long = 0L
)