
package com.app.core.domain.model

/**
 * Coordenada geográfica perteneciente a una ruta
 * ajustada mediante un servicio de Map Matching.
 *
 * No representa una ubicación GPS original.
 */
data class RouteCoordinate(

    val latitude: Double,

    val longitude: Double

) {

    init {
        require(latitude in -90.0..90.0) {
            "La latitud debe estar entre -90 y 90."
        }

        require(longitude in -180.0..180.0) {
            "La longitud debe estar entre -180 y 180."
        }
    }
}

/**
 * Representa un segmento continuo de una ruta ajustada.
 *
 * Se utiliza una lista de segmentos para evitar conectar
 * artificialmente recorridos que OSRM haya separado.
 */
data class MatchedRouteSegment(

    val coordinates: List<RouteCoordinate>,

    /**
     * Confianza estimada por OSRM para este segmento.
     * Su valor está comprendido entre 0 y 1.
     */
    val confidence: Double? = null

) {

    init {
        require(coordinates.size >= 2) {
            "Un segmento debe contener al menos dos coordenadas."
        }

        require(confidence == null || confidence in 0.0..1.0) {
            "La confianza debe estar entre 0 y 1."
        }
    }
}

/**
 * Resultado completo del proceso de Map Matching.
 *
 * Puede contener uno o varios segmentos continuos.
 * Este modelo se utiliza exclusivamente para representar
 * la geometría ajustada en el mapa.
 *
 * No reemplaza ni modifica el historial GPS almacenado.
 */
data class MatchedRoute(

    val segments: List<MatchedRouteSegment>

) {

    /**
     * Indica si existe al menos un segmento válido
     * para representar en el mapa.
     */
    val hasGeometry: Boolean
        get() = segments.isNotEmpty()

    /**
     * Cantidad total de coordenadas de la geometría.
     */
    val coordinateCount: Int
        get() = segments.sumOf { it.coordinates.size }
}
