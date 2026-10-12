package com.app.core.domain.usecase

import com.app.core.domain.model.Walk
import com.app.core.domain.repository.PetRepository
import com.app.core.domain.repository.WalkRepository

class GetPetWalkHistoryUseCase(
    private val petRepository: PetRepository,
    private val walkRepository: WalkRepository
) {
    suspend operator fun invoke(
        petId: String
    ): List<Walk> {
        require(petId.isNotBlank()) {
            "El identificador de la mascota no puede estar vacío"
        }

        val pet = petRepository.getPetById(petId)
            ?: throw IllegalArgumentException(
                "No existe una mascota con ese identificador"
            )

        val ownerWalks = walkRepository.getWalksByOwner(pet.ownerId)

        return ownerWalks.filter { walk ->
            petId in walk.petIds
        }
    }
}