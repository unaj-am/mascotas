package com.app.core.domain.usecase

import com.app.core.domain.model.Walker
import com.app.core.domain.repository.WalkerRepository

class SearchWalkersUseCase(
    private val walkerRepository: WalkerRepository
) {
    suspend operator fun invoke(
        serviceArea: String? = null,
        maxPrice: Double? = null,
        minExperienceYears: Int? = null
    ): List<Walker> {
        require(maxPrice == null || maxPrice >= 0.0) {
            "La tarifa máxima no puede ser negativa"
        }

        require(minExperienceYears == null || minExperienceYears >= 0) {
            "La experiencia mínima no puede ser negativa"
        }

        var walkers = walkerRepository.getAvailableWalkers()

        if (!serviceArea.isNullOrBlank()) {
            walkers = walkers.filter { walker ->
                walker.serviceArea.contains(
                    serviceArea.trim(),
                    ignoreCase = true
                )
            }
        }

        if (maxPrice != null) {
            walkers = walkers.filter { walker ->
                walker.pricePerWalk <= maxPrice
            }
        }

        if (minExperienceYears != null) {
            walkers = walkers.filter { walker ->
                walker.experienceYears >= minExperienceYears
            }
        }

        return walkers
    }
}