
package com.app.core.domain.usecase

import com.app.core.domain.model.WalkLocation
import com.app.core.domain.model.WalkStatus
import com.app.core.domain.repository.WalkLocationRepository
import com.app.core.domain.repository.WalkRepository
import com.app.core.domain.usecase.auth.GetCurrentUserIdUseCase
import javax.inject.Inject

/**
 * Caso de uso responsable de publicar una ubicación
 * geográfica asociada a un paseo.
 *
 * Antes de publicar verifica:
 * - Que exista un usuario autenticado.
 * - Que el identificador del paseo sea válido.
 * - Que el paseo exista.
 * - Que el usuario sea el paseador asignado.
 * - Que el paseo esté en curso.
 */
class PublishWalkLocationUseCase @Inject constructor(
    private val walkLocationRepository: WalkLocationRepository,
    private val walkRepository: WalkRepository,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase
) {

    suspend operator fun invoke(
        walkId: String,
        location: WalkLocation
    ) {
        require(walkId.isNotBlank()) {
            "El identificador del paseo no puede estar vacío."
        }

        // Identificamos al usuario autenticado.
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

        // Verificamos que el usuario sea el paseador asignado.
        require(walk.walkerId == currentUserId) {
            "Solo el paseador asignado puede publicar ubicaciones."
        }

        // Verificamos que el paseo esté en curso.
        require(walk.status == WalkStatus.IN_PROGRESS) {
            "Solo se pueden publicar ubicaciones de paseos en curso."
        }

        // Publicamos la ubicación.
        walkLocationRepository.publishLocation(
            walkId = walkId,
            location = location
        )
    }
}
