package com.app.core.domain.usecase

import com.app.core.domain.model.Pet
import com.app.core.domain.repository.PetRepository

class GetPetsByOwnerUseCase(
    private val petRepository: PetRepository
) {

    suspend operator fun invoke(
        ownerId: String
    ): List<Pet> {

        require(ownerId.isNotBlank()) {
            "El identificador del dueño no puede estar vacío"
        }

        return petRepository.getPetsByOwner(ownerId)
    }
}