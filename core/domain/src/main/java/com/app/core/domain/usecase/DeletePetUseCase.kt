package com.app.core.domain.usecase

import com.app.core.domain.repository.PetRepository
import javax.inject.Inject

class DeletePetUseCase @Inject constructor(
    private val petRepository: PetRepository
) {
    suspend operator fun invoke(petId: String) {
        require(petId.isNotBlank()) {
            "El identificador de la mascota no puede estar vacío"
        }
        petRepository.deletePet(petId)
    }
}