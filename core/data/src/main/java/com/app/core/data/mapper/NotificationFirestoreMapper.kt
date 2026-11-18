package com.app.core.data.mapper

import com.app.core.domain.model.AppNotification
import com.app.core.domain.model.NotificationType
import com.google.firebase.firestore.DocumentSnapshot
import java.time.Instant

fun DocumentSnapshot.requestNotificationsForOwner(): List<AppNotification> {
    return when (getString("status").orEmpty()) {
        "ACCEPTED" -> listOfNotNull(
            buildEventNotification(
                id = "event_request_accepted_$id",
                userId = getString("ownerId").orEmpty(),
                title = "Paseo aceptado",
                body = "Tu paseo fue aceptado.",
                type = NotificationType.WALK_ACCEPTED,
                referenceId = id,
                createdAt = timestampMillis("acceptedAt")
            )
        )

        "REJECTED" -> listOfNotNull(
            buildEventNotification(
                id = "event_request_rejected_$id",
                userId = getString("ownerId").orEmpty(),
                title = "Solicitud rechazada",
                body = "Tu solicitud de paseo fue rechazada.",
                type = NotificationType.WALK_REJECTED,
                referenceId = id,
                createdAt = timestampMillis("rejectedAt")
            )
        )

        else -> emptyList()
    }
}

fun DocumentSnapshot.requestNotificationsForWalker(): List<AppNotification> {
    val notifications = mutableListOf<AppNotification>()

    // Se conserva el evento de creación aunque cambie el estado posterior.
    buildEventNotification(
        id = "event_request_created_$id",
        userId = getString("walkerId").orEmpty(),
        title = "Nueva solicitud",
        body = "Tenés una nueva solicitud de paseo.",
        type = NotificationType.WALK_REQUEST_CREATED,
        referenceId = id,
        createdAt = timestampMillis("createdAt")
    )?.let(notifications::add)

    if (getString("status") == "CANCELLED") {
        buildEventNotification(
            id = "event_request_cancelled_$id",
            userId = getString("walkerId").orEmpty(),
            title = "Solicitud cancelada",
            body = "El dueño canceló el paseo.",
            type = NotificationType.WALK_REQUEST_CANCELLED,
            referenceId = id,
            createdAt = timestampMillis("cancelledAt")
        )?.let(notifications::add)
    }

    return notifications
}

fun DocumentSnapshot.walkNotificationsForOwner(): List<AppNotification> {
    val ownerId = getString("ownerId").orEmpty()

    return listOfNotNull(
        buildEventNotification(
            id = "event_walk_started_$id",
            userId = ownerId,
            title = "Paseo iniciado",
            body = "Tu mascota ya está de paseo.",
            type = NotificationType.WALK_STARTED,
            referenceId = id,
            createdAt = instantStringMillis("startedAt")
        ),
        buildEventNotification(
            id = "event_walk_completed_$id",
            userId = ownerId,
            title = "Paseo finalizado",
            body = "Tu mascota terminó su paseo.",
            type = NotificationType.WALK_COMPLETED,
            referenceId = id,
            createdAt = instantStringMillis("finishedAt")
        )
    )
}

fun DocumentSnapshot.walkNotificationsForWalker(): List<AppNotification> {
    if (getString("status") != "CANCELLED") return emptyList()

    // Walk aún no almacena cancelledAt; se aceptan Timestamp e ISO-8601.
    val cancelledAt = timestampMillis("cancelledAt")
        ?: instantStringMillis("cancelledAt")

    return listOfNotNull(
        buildEventNotification(
            id = "event_walk_cancelled_$id",
            userId = getString("walkerId").orEmpty(),
            title = "Paseo cancelado",
            body = "El dueño canceló el paseo.",
            type = NotificationType.WALK_CANCELLED,
            referenceId = id,
            createdAt = cancelledAt
        )
    )
}

private fun buildEventNotification(
    id: String,
    userId: String,
    title: String,
    body: String,
    type: String,
    referenceId: String,
    createdAt: Long?
): AppNotification? {
    if (userId.isBlank() || createdAt == null) return null

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

private fun DocumentSnapshot.timestampMillis(field: String): Long? =
    getTimestamp(field)?.toDate()?.time

private fun DocumentSnapshot.instantStringMillis(field: String): Long? {
    val value = getString(field)?.takeIf(String::isNotBlank) ?: return null
    return runCatching { Instant.parse(value).toEpochMilli() }.getOrNull()
}
