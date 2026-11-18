package com.app.core.data.mapper

import com.app.core.domain.model.RequestStatus
import com.app.core.domain.model.WalkRequest
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue

fun DocumentSnapshot.toWalkRequest(): WalkRequest =
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
        status = getString("status")?.let { value ->
            runCatching { RequestStatus.valueOf(value) }.getOrNull()
        } ?: RequestStatus.PENDING,
        observations = getString("observations").orEmpty()
    )

fun WalkRequest.toFirestoreMap(
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
        "observations" to observations,
        "createdAt" to FieldValue.serverTimestamp()
    )
