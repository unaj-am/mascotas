package com.app.core.domain.usecase

import com.app.core.domain.model.Walker
import com.app.core.domain.repository.WalkerRepository

class SaveWalkerUseCase(
    private val walkerRepository: WalkerRepository
) {
    suspend operator fun invoke(walker: Walker) {
        require(walker.userId.isNotBlank()) {
            "El identificador del usuario es obligatorio"
        }
        require(walker.description.isNotBlank()) {
            "La descripción del paseador es obligatoria"
        }
        require(walker.experienceYears >= 0) {
            "Los años de experiencia no pueden ser negativos"
        }
        require(walker.serviceArea.isNotBlank()) {
            "La zona de servicio es obligatoria"
        }
        require(walker.pricePerWalk >= 0) {
            "El precio por paseo no puede ser negativo"
        }
        require(walker.maxPets > 0) {
            "La cantidad máxima de mascotas debe ser mayor que cero"
        }
        require(walker.rating == null || walker.rating in 0.0..5.0) {
            "La calificación debe estar entre 0 y 5"
        }

        walkerRepository.saveWalker(walker)
    }
}