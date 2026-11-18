package com.app.core.data.mapper

import com.app.core.domain.model.Walker
import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.toWalker(): Walker =
    Walker(
        userId = id,
        description = getString("description").orEmpty(),
        experienceYears = getLong("experienceYears")?.toInt() ?: 0,
        serviceArea = getString("serviceArea").orEmpty(),
        available = getBoolean("available") ?: false,
        pricePerWalk = getDouble("pricePerWalk") ?: 0.0,
        maxPets = getLong("maxPets")?.toInt() ?: 1,
        rating = getDouble("rating")
    )

fun Walker.toFirestoreMap(): Map<String, Any?> =
    mapOf(
        "description" to description,
        "experienceYears" to experienceYears,
        "serviceArea" to serviceArea,
        "available" to available,
        "pricePerWalk" to pricePerWalk,
        "maxPets" to maxPets,
        "rating" to rating
    )

fun Walker.toFirestoreUpdateMap(): Map<String, Any> =
    mapOf(
        "description" to description,
        "experienceYears" to experienceYears,
        "serviceArea" to serviceArea,
        "available" to available,
        "pricePerWalk" to pricePerWalk,
        "maxPets" to maxPets
    )
