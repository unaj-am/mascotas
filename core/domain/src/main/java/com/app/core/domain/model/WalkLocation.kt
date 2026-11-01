
package com.app.core.domain.model

/**
 * Representa una ubicación geográfica asociada
 * a un paseo realizado por un paseador.
 *
 * Este modelo pertenece al dominio y no depende
 * de Android, Firebase ni de ninguna tecnología externa.
 */
data class WalkLocation(

    /**
     * Latitud geográfica.
     */
    val latitude: Double,

    /**
     * Longitud geográfica.
     */
    val longitude: Double,

    /**
     * Precisión estimada de la ubicación en metros.
     */
    val accuracy: Float,

    /**
     * Dirección del movimiento en grados.
     * Puede ser null si no está disponible.
     */
    val bearing: Float? = null,

    /**
     * Velocidad del paseador en metros por segundo.
     * Puede ser null si no está disponible.
     */
    val speed: Float? = null,

    /**
     * Fecha y hora en que se registró la posición,
     * expresada en milisegundos desde Unix Epoch.
     */
    val recordedAt: Long
) {

    init {
        require(latitude in -90.0..90.0) {
            "La latitud debe estar entre -90 y 90."
        }

        require(longitude in -180.0..180.0) {
            "La longitud debe estar entre -180 y 180."
        }

        require(accuracy >= 0f) {
            "La precisión no puede ser negativa."
        }

        require(bearing == null || bearing in 0f..360f) {
            "La dirección debe estar entre 0 y 360 grados."
        }

        require(speed == null || speed >= 0f) {
            "La velocidad no puede ser negativa."
        }

        require(recordedAt > 0L) {
            "La fecha de registro debe ser válida."
        }
    }
}
