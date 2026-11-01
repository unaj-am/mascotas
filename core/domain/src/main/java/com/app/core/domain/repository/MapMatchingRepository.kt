
package com.app.core.domain.repository

import com.app.core.domain.model.MatchedRoute
import com.app.core.domain.model.WalkLocation

/**
 * Contrato responsable de ajustar una secuencia de
 * ubicaciones GPS a la red vial mediante Map Matching.
 *
 * Separa el proceso de ajuste geográfico de la lógica
 * de presentación y de almacenamiento GPS.
 *
 * No conoce OSRM, Firebase ni Android.
 */
interface MapMatchingRepository {

    /**
     * Ajusta las ubicaciones GPS recibidas a la red vial.
     *
     * @param locations Secuencia de ubicaciones GPS originales,
     * ordenadas cronológicamente.
     *
     * @return La geometría ajustada o null si no fue posible
     * obtener un recorrido válido.
     *
     * La implementación no debe modificar ni almacenar
     * las ubicaciones GPS originales.
     */
    suspend fun matchRoute(
        locations: List<WalkLocation>
    ): MatchedRoute?
}
