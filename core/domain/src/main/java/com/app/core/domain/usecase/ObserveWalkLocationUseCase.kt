
package com.app.core.domain.usecase

import com.app.core.domain.repository.WalkLocationRepository
import com.app.core.domain.repository.WalkRepository
import com.app.core.domain.usecase.auth.GetCurrentUserIdUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/**
 * Caso de uso responsable de observar en tiempo real
 * la última ubicación registrada de un paseo.
 *
 * Solamente permite consultar ubicaciones a los usuarios
 * que participan del paseo:
 * - Dueño.
 * - Paseador asignado.
 */
class ObserveWalkLocationUseCase @Inject constructor(
    private val walkLocationRepository: WalkLocationRepository,
    private val walkRepository: WalkRepository,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase
) {

    operator fun invoke(
        walkId: String
    ): Flow<com.app.core.domain.model.WalkLocation?> = flow {

        require(walkId.isNotBlank()) {
            "El identificador del paseo no puede estar vacío."
        }

        // Obtenemos el usuario autenticado.
        val currentUserId = requireNotNull(
            getCurrentUserIdUseCase()
        ) {
            "No hay un usuario autenticado."
        }

        // Consultamos el paseo.
        val walk = requireNotNull(
            walkRepository.getWalkById(walkId)
        ) {
            "No existe un paseo con ese identificador."
        }

        val isOwner = walk.ownerId == currentUserId
        val isWalker = walk.walkerId == currentUserId

        require(isOwner || isWalker) {
            "No tenés autorización para consultar la ubicación de este paseo."
        }

        // Delegamos la observación continua al repositorio.
        emitAll(
            walkLocationRepository.observeCurrentLocation(walkId)
        )
    }
}
