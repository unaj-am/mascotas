
package com.app.core.domain.usecase

import com.app.core.domain.model.Walk
import com.app.core.domain.repository.WalkRequestRepository
import javax.inject.Inject

class AcceptWalkRequestUseCase @Inject constructor(
    private val walkRequestRepository: WalkRequestRepository
) {

    suspend operator fun invoke(
        requestId: String
    ): Walk {
        require(requestId.isNotBlank()) {
            "El identificador de la solicitud es obligatorio"
        }

        return walkRequestRepository.acceptRequestAndCreateWalk(
            requestId = requestId
        )
    }
}
