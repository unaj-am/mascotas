package com.app.core.database.mapper

import com.app.core.database.model.PetEntity
import com.app.core.domain.model.Pet

fun PetEntity.toDomain(): Pet {
    return Pet(
        id = id,
        ownerId = ownerId,
        name = name,
        breed = breed,
        age = age,
        weight = weight,
        observations = observations,
        photoUri = photoUri,
        size = size,
        behavior = behavior,
        specialNeeds = specialNeeds
    )
}

fun Pet.toEntity(): PetEntity {
    return PetEntity(
        id = id,
        ownerId = ownerId,
        name = name,
        breed = breed,
        age = age,
        weight = weight,
        observations = observations,
        photoUri = photoUri,
        size = size,
        behavior = behavior,
        specialNeeds = specialNeeds
    )
}
