package com.app.core.data.mapper

import com.app.core.domain.model.WalkLocation
import com.google.firebase.firestore.DocumentSnapshot

fun WalkLocation.toFirestoreMap(): Map<String, Any?> =
    mapOf(
        "latitude" to latitude,
        "longitude" to longitude,
        "accuracy" to accuracy.toDouble(),
        "bearing" to bearing?.toDouble(),
        "speed" to speed?.toDouble(),
        "recordedAt" to recordedAt
    )

fun DocumentSnapshot.toWalkLocation(): WalkLocation {
    val latitude = getDouble("latitude")
        ?: throw IllegalStateException("La ubicación no contiene latitud")
    val longitude = getDouble("longitude")
        ?: throw IllegalStateException("La ubicación no contiene longitud")
    val accuracy = getDouble("accuracy")
        ?: throw IllegalStateException("La ubicación no contiene precisión")
    val recordedAt = getLong("recordedAt")
        ?: throw IllegalStateException("La ubicación no contiene fecha de registro")

    return WalkLocation(
        latitude = latitude,
        longitude = longitude,
        accuracy = accuracy.toFloat(),
        bearing = getDouble("bearing")?.toFloat(),
        speed = getDouble("speed")?.toFloat(),
        recordedAt = recordedAt
    )
}
