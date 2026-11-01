
package com.app.core.location.usecase

import com.app.core.domain.model.WalkLocation
import com.app.core.domain.usecase.PublishWalkLocationUseCase
import com.app.core.location.LocationProvider
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlinx.coroutines.flow.collect

class TrackWalkLocationUseCase @Inject constructor(
    private val locationProvider: LocationProvider,
    private val publishWalkLocationUseCase: PublishWalkLocationUseCase
) {

    companion object {
        private const val MAX_ACCURACY_METERS = 35f
        private const val MIN_DISTANCE_METERS = 5.0
        private const val ACCURACY_DISTANCE_FACTOR = 0.5
        private const val MAX_INTERVAL_WITHOUT_UPDATE = 30_000L

        private const val EARTH_RADIUS_METERS = 6_371_000.0
    }

    suspend operator fun invoke(walkId: String) {

        require(walkId.isNotBlank()) {
            "El identificador del paseo es obligatorio"
        }

        var previousPublishedLocation: WalkLocation? = null

        locationProvider.getLocationUpdates(
            intervalMillis = 5_000L,
            fastestIntervalMillis = 3_000L
        ).collect { locationData ->

            // 1. Descartar ubicaciones poco precisas.
            if (locationData.accuracy > MAX_ACCURACY_METERS) {
                return@collect
            }

            val currentLocation = WalkLocation(
                latitude = locationData.latitude,
                longitude = locationData.longitude,
                accuracy = locationData.accuracy,
                bearing = locationData.bearing,
                speed = locationData.speed,
                recordedAt = locationData.timestamp
            )

            val previousLocation = previousPublishedLocation

            if (previousLocation != null) {

                // Evitar publicar ubicaciones fuera de orden.
                if (currentLocation.recordedAt <= previousLocation.recordedAt) {
                    return@collect
                }

                val elapsedTime =
                    currentLocation.recordedAt - previousLocation.recordedAt

                val distance = calculateDistance(
                    previousLocation,
                    currentLocation
                )

                // 2. Umbral dinámico según la precisión GPS.
                val minimumDistance = maxOf(
                    MIN_DISTANCE_METERS,
                    currentLocation.accuracy.toDouble() *
                            ACCURACY_DISTANCE_FACTOR
                )

                val insignificantMovement = distance < minimumDistance

                // 3. Evitar ruido GPS, pero mantener actualizaciones
                // periódicas incluso cuando el paseador está detenido.
                if (
                    insignificantMovement &&
                    elapsedTime < MAX_INTERVAL_WITHOUT_UPDATE
                ) {
                    return@collect
                }
            }

            // Publicar ubicación actual e historial.
            publishWalkLocationUseCase(
                walkId = walkId,
                location = currentLocation
            )

            // Actualizar referencia solamente después de publicar.
            previousPublishedLocation = currentLocation
        }
    }

    private fun calculateDistance(
        first: WalkLocation,
        second: WalkLocation
    ): Double {

        val latitude1 = Math.toRadians(first.latitude)
        val latitude2 = Math.toRadians(second.latitude)

        val latitudeDifference =
            Math.toRadians(second.latitude - first.latitude)

        val longitudeDifference =
            Math.toRadians(second.longitude - first.longitude)

        val a =
            sin(latitudeDifference / 2) *
                    sin(latitudeDifference / 2) +
                    cos(latitude1) *
                    cos(latitude2) *
                    sin(longitudeDifference / 2) *
                    sin(longitudeDifference / 2)

        val c = 2 * atan2(
            sqrt(a),
            sqrt(1 - a)
        )

        return EARTH_RADIUS_METERS * c
    }
}
