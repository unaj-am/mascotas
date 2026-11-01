
package com.app.core.location

import com.app.core.location.model.LocationData
import kotlinx.coroutines.flow.Flow

/**
 * Contrato responsable de proporcionar actualizaciones
 * de ubicación del dispositivo.
 *
 * No conoce Firebase, Firestore ni los paseos.
 * Solamente expone las posiciones obtenidas mediante GPS.
 */
interface LocationProvider {

    /**
     * Comienza a emitir actualizaciones de ubicación.
     *
     * @param intervalMillis Intervalo deseado entre actualizaciones.
     * @param fastestIntervalMillis Intervalo mínimo aceptable
     * entre actualizaciones.
     *
     * @return Flow que emite nuevas posiciones GPS.
     *
     * La colección debe cancelarse cuando ya no se necesite
     * obtener ubicaciones.
     */
    fun getLocationUpdates(
        intervalMillis: Long = 5_000L,
        fastestIntervalMillis: Long = 3_000L
    ): Flow<LocationData>
}
