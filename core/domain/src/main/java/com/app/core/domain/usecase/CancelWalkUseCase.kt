
package com.app.core.domain.usecase

import com.app.core.domain.repository.WalkRepository
import javax.inject.Inject

class CancelWalkUseCase @Inject constructor(
    private val walkRepository: WalkRepository
) {
    suspend operator fun invoke(walkId: String) {
        require(walkId.isNotBlank()) {
            "El identificador del paseo no puede estar vacío"
        }

        walkRepository.cancelWalk(walkId)
    }
}
