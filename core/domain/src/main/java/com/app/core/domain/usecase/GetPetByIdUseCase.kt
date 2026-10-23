package com.app.core.domain.usecase

import com.app.core.domain.model.Pet
import com.app.core.domain.repository.PetRepository
import javax.inject.Inject

class GetPetByIdUseCase @Inject constructor(
    private val petRepository: PetRepository
) {
    suspend operator fun invoke(petId: String): Pet? {
        require(petId.isNotBlank()) {
            "El identificador de la mascota no puede estar vacío"
        }
        return petRepository.getPetById(petId)
    }
}