package com.app.core.data.mapper

import com.app.core.domain.model.Pet
import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.toPet(): Pet =
    Pet(
        id = id,
        ownerId = getString("ownerId")
            ?: throw IllegalStateException("La mascota no tiene dueño"),
        name = getString("name").orEmpty(),
        breed = getString("breed").orEmpty(),
        age = getLong("age")?.toInt() ?: 0,
        weight = getDouble("weight") ?: 0.0,
        observations = getString("observations").orEmpty(),
        photoUri = getString("photoUri"),
        size = getString("size").orEmpty(),
        behavior = getString("behavior").orEmpty(),
        specialNeeds = getString("specialNeeds").orEmpty()
    )

fun Pet.toFirestoreMap(): Map<String, Any?> =
    mapOf(
        "ownerId" to ownerId,
        "name" to name,
        "breed" to breed,
        "age" to age,
        "weight" to weight,
        "observations" to observations,
        "photoUri" to photoUri,
        "size" to size,
        "behavior" to behavior,
        "specialNeeds" to specialNeeds
    )
