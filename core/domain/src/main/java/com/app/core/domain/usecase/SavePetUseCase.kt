package com.app.core.domain.usecase

import com.app.core.domain.model.Pet
import com.app.core.domain.repository.PetRepository
import javax.inject.Inject

class SavePetUseCase @Inject constructor(
    private val petRepository: PetRepository
) {
    suspend operator fun invoke(pet: Pet) {
        require(pet.id.isNotBlank()) {
            "El identificador de la mascota es obligatorio"
        }
        require(pet.ownerId.isNotBlank()) {
            "El identificador del dueño es obligatorio"
        }
        require(pet.name.isNotBlank()) {
            "El nombre de la mascota es obligatorio"
        }
        require(pet.age >= 0) {
            "La edad no puede ser negativa"
        }
        require(pet.weight > 0) {
            "El peso debe ser mayor que cero"
        }
        petRepository.savePet(pet)
    }
}