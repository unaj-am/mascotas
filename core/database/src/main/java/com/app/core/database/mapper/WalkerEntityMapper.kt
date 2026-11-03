
package com.app.core.database.mapper

import com.app.core.database.model.WalkerEntity
import com.app.core.domain.model.Walker

fun WalkerEntity.toDomain(): Walker {
    return Walker(
        userId = userId,
        description = description,
        experienceYears = experienceYears,
        serviceArea = serviceArea,
        available = available,
        pricePerWalk = pricePerWalk,
        maxPets = maxPets,
        rating = rating
    )
}

fun Walker.toEntity(): WalkerEntity {
    return WalkerEntity(
        userId = userId,
        description = description,
        experienceYears = experienceYears,
        serviceArea = serviceArea,
        available = available,
        pricePerWalk = pricePerWalk,
        maxPets = maxPets,
        rating = rating
    )
}
