
package com.app.core.location.model

/**
 * Representa una lectura de ubicación obtenida desde el dispositivo.
 *
 * Este modelo pertenece a core:location porque representa
 * información técnica proporcionada por el GPS.
 *
 * No depende de Android ni de Firebase.
 */
data class LocationData(

    /**
     * Latitud geográfica.
     * Rango válido: -90 a 90.
     */
    val latitude: Double,

    /**
     * Longitud geográfica.
     * Rango válido: -180 a 180.
     */
    val longitude: Double,

    /**
     * Precisión estimada de la ubicación en metros.
     */
    val accuracy: Float,

    /**
     * Dirección del movimiento en grados.
     * Valores entre 0 y 360.
     *
     * Puede ser null cuando no existe información
     * suficiente sobre la dirección.
     */
    val bearing: Float? = null,

    /**
     * Velocidad del dispositivo en metros por segundo.
     *
     * Puede ser null cuando no está disponible.
     */
    val speed: Float? = null,

    /**
     * Fecha y hora de captura en milisegundos
     * desde Unix Epoch.
     */
    val timestamp: Long
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

        require(timestamp > 0L) {
            "El timestamp debe ser válido."
        }
    }
}
