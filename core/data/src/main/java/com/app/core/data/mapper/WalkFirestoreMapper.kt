package com.app.core.data.mapper

import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkStatus
import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.toWalk(): Walk =
    Walk(
        id = id,
        ownerId = getString("ownerId")
            ?: throw IllegalStateException("El paseo no tiene dueño"),
        walkerId = getString("walkerId")
            ?: throw IllegalStateException("El paseo no tiene paseador"),
        petIds = get("petIds") as? List<String> ?: emptyList(),
        scheduledAt = getString("scheduledAt").orEmpty(),
        durationMinutes = getLong("durationMinutes")?.toInt() ?: 0,
        status = getString("status")?.let { value ->
            runCatching { WalkStatus.valueOf(value) }.getOrNull()
        } ?: WalkStatus.SCHEDULED,
        startedAt = getString("startedAt")?.takeIf(String::isNotBlank),
        finishedAt = getString("finishedAt")?.takeIf(String::isNotBlank),
        distanceKm = getDouble("distanceKm")?.takeIf { it > 0.0 },
        observations = getString("observations").orEmpty()
    )

// Esta forma preserva los valores nulos que ya serializa el flujo de solicitudes.
fun Walk.toFirestoreMap(): Map<String, Any?> =
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

// El flujo remoto existente serializa campos opcionales con sus valores por defecto.
fun Walk.toFirestoreMapWithDefaults(): Map<String, Any> =
    mapOf(
        "ownerId" to ownerId,
        "walkerId" to walkerId,
        "petIds" to petIds,
        "scheduledAt" to scheduledAt,
        "durationMinutes" to durationMinutes,
        "status" to status.name,
        "startedAt" to (startedAt ?: ""),
        "finishedAt" to (finishedAt ?: ""),
        "distanceKm" to (distanceKm ?: 0.0),
        "observations" to observations
    )
