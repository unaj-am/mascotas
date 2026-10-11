package com.app.core.domain.repository

import com.app.core.domain.model.Pet

interface PetRepository {

    suspend fun getPetsByOwner(
        ownerId: String
    ): List<Pet>

    suspend fun getPetById(
        petId: String
    ): Pet?

    suspend fun savePet(
        pet: Pet
    )

    suspend fun deletePet(
        petId: String
    )
}