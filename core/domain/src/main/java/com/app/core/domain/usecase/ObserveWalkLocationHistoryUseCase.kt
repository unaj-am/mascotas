package com.app.core.domain.usecase

import com.app.core.domain.repository.WalkLocationRepository
import com.app.core.domain.repository.WalkRepository
import com.app.core.domain.usecase.auth.GetCurrentUserIdUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

class ObserveWalkLocationHistoryUseCase @Inject constructor(
    private val walkLocationRepository: WalkLocationRepository,
    private val walkRepository: WalkRepository,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase
) {

    operator fun invoke(walkId: String): Flow<List<com.app.core.domain.model.WalkLocation>> =
        flow {
            require(walkId.isNotBlank()) {
                "El identificador del paseo es obligatorio"
            }

            val currentUserId = requireNotNull(
                getCurrentUserIdUseCase()
            ) {
                "Debés iniciar sesión para consultar el recorrido"
            }

            val walk = requireNotNull(
                walkRepository.getWalkById(walkId)
            ) {
                "No se encontró el paseo solicitado"
            }

            val isOwner = walk.ownerId == currentUserId
            val isWalker = walk.walkerId == currentUserId

            require(isOwner || isWalker) {
                "No tenés permiso para consultar este recorrido"
            }

            emitAll(
                walkLocationRepository.observeLocationHistory(walkId)
            )
        }
}