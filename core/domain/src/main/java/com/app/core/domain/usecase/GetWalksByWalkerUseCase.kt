package com.app.core.domain.usecase

import com.app.core.domain.model.Walk
import com.app.core.domain.repository.WalkRepository
import javax.inject.Inject

class GetWalksByWalkerUseCase @Inject constructor(
    private val walkRepository: WalkRepository
) {
    suspend operator fun invoke(walkerId: String): List<Walk> {
        require(walkerId.isNotBlank()) {
            "El identificador del paseador no puede estar vacío"
        }

        return walkRepository.getWalksByWalker(walkerId)
    }
}