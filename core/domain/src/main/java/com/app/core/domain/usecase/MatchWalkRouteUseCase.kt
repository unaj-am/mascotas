package com.app.core.domain.usecase

import com.app.core.domain.model.MatchedRoute
import com.app.core.domain.model.WalkLocation
import com.app.core.domain.repository.MapMatchingRepository
import javax.inject.Inject

/**
 * Caso de uso responsable de solicitar el ajuste
 * de un recorrido GPS mediante Map Matching.
 *
 * Recibe las ubicaciones originales del paseo y
 * devuelve una geometría ajustada a las calles.
 *
 * No modifica ni persiste las ubicaciones originales.
 */
class MatchWalkRouteUseCase @Inject constructor(
    private val mapMatchingRepository: MapMatchingRepository
) {

    companion object {
        private const val MIN_LOCATIONS = 5
    }

    suspend operator fun invoke(
        locations: List<WalkLocation>
    ): MatchedRoute? {

        // No solicitamos el ajuste con muy pocos puntos.
        if (locations.size < MIN_LOCATIONS) {
            return null
        }

        // Eliminamos registros repetidos y ordenamos
        // cronológicamente antes de enviarlos a OSRM.
        val validLocations = locations
            .distinctBy { it.recordedAt }
            .sortedBy { it.recordedAt }

        if (validLocations.size < MIN_LOCATIONS) {
            return null
        }

        return mapMatchingRepository.matchRoute(validLocations)
    }
}