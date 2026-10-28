package com.app.core.domain.usecase

import com.app.core.domain.repository.WalkRepository
import javax.inject.Inject

class GetWalkByIdUseCase @Inject constructor(
    private val walkRepository: WalkRepository
) {
    suspend operator fun invoke(walkId: String) =
        walkRepository.getWalkById(walkId)
}