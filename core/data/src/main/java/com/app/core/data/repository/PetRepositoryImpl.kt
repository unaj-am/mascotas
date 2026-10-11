package com.app.core.data.repository

import com.app.core.domain.model.Pet
import com.app.core.domain.repository.PetRepository

class PetRepositoryImpl : PetRepository {

    private val pets = mutableListOf<Pet>()

    override suspend fun getPetsByOwner(ownerId: String): List<Pet> {
        return pets.filter { it.ownerId == ownerId }
    }

    override suspend fun getPetById(petId: String): Pet? {
        return pets.find { it.id == petId }
    }

    override suspend fun savePet(pet: Pet) {
        val index = pets.indexOfFirst { it.id == pet.id }

        if (index >= 0) {
            pets[index] = pet
        } else {
            pets.add(pet)
        }
    }

    override suspend fun deletePet(petId: String) {
        pets.removeAll { it.id == petId }
    }
}