
package com.app.core.domain.repository

import com.app.core.domain.model.WalkLocation
import kotlinx.coroutines.flow.Flow

/**
 * Contrato responsable de administrar las ubicaciones
 * geográficas asociadas a un paseo.
 *
 * Separa el seguimiento GPS de las operaciones generales
 * del WalkRepository.
 *
 * No conoce Firebase ni Android.
 */
interface WalkLocationRepository {

    /**
     * Publica una nueva ubicación del paseador.
     *
     * La implementación deberá verificar que el usuario
     * autenticado sea el paseador asignado al paseo y que
     * el paseo se encuentre en estado IN_PROGRESS.
     */
    suspend fun publishLocation(
        walkId: String,
        location: WalkLocation
    )

    /**
     * Obtiene la última ubicación registrada de un paseo.
     *
     * Devuelve null si todavía no existen ubicaciones.
     */
    suspend fun getLastLocation(
        walkId: String
    ): WalkLocation?

    /**
     * Observa continuamente la última ubicación del paseo.
     *
     * Emite una nueva posición cada vez que el paseador
     * publica una actualización.
     */
    fun observeCurrentLocation(
        walkId: String
    ): Flow<WalkLocation?>

    /**
     * Observa el historial de ubicaciones del paseo.
     *
     * Permite reconstruir el recorrido realizado por
     * el paseador sobre el mapa.
     *
     * Emite una lista actualizada cuando se registran
     * nuevas posiciones.
     */
    fun observeLocationHistory(
        walkId: String
    ): Flow<List<WalkLocation>>
}
