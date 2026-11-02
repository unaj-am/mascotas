
package com.app.features.tracking.component

import com.app.core.domain.model.WalkLocation
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object WalkRouteProcessor {

    private const val MAX_ROUTE_ACCURACY_METERS = 35f
    private const val MIN_ROUTE_DISTANCE_METERS = 4.0
    private const val MAX_ROUTE_DISTANCE_METERS = 10.0
    private const val EARTH_RADIUS_METERS = 6_371_000.0

    /**
     * Procesa el historial GPS para preparar una geometría
     * más limpia para su representación en el mapa.
     *
     * No modifica la colección original.
     */
    fun filterLocations(
        history: List<WalkLocation>,
        currentLocation: WalkLocation?
    ): List<WalkLocation> {

        val allLocations = (history + listOfNotNull(currentLocation))
            .distinctBy { it.recordedAt }
            .sortedBy { it.recordedAt }

        val validLocations = allLocations.filter { location ->
            location.accuracy >= 0f &&
                    location.accuracy <= MAX_ROUTE_ACCURACY_METERS &&
                    location.latitude.isFinite() &&
                    location.longitude.isFinite() &&
                    location.latitude in -90.0..90.0 &&
                    location.longitude in -180.0..180.0
        }

        if (validLocations.size <= 1) {
            return validLocations
        }

        val filteredLocations = mutableListOf<WalkLocation>()

        filteredLocations.add(validLocations.first())

        for (index in 1 until validLocations.size) {
            val candidate = validLocations[index]
            val previous = filteredLocations.last()

            val distance = calculateDistanceMeters(
                first = previous,
                second = candidate
            )

            val accuracyThreshold = maxOf(
                previous.accuracy,
                candidate.accuracy
            ) * 0.35

            val minimumDistance = accuracyThreshold.coerceIn(
                MIN_ROUTE_DISTANCE_METERS,
                MAX_ROUTE_DISTANCE_METERS
            )

            if (distance >= minimumDistance) {
                filteredLocations.add(candidate)
            }
        }

        return filteredLocations
    }

    /**
     * Calcula la distancia geográfica entre dos posiciones
     * utilizando la fórmula de Haversine.
     */
    private fun calculateDistanceMeters(
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
            sqrt(a.coerceIn(0.0, 1.0)),
            sqrt((1.0 - a).coerceIn(0.0, 1.0))
        )

        return EARTH_RADIUS_METERS * c
    }
}
