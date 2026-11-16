package com.app.features.tracking.component

import com.app.core.domain.model.RouteCoordinate
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.max

data class RouteCameraViewport(
    val latitude: Double,
    val longitude: Double,
    val zoom: Double
)

object RouteCameraViewportCalculator {

    private const val TILE_SIZE_PIXELS = 256.0
    private const val MIN_ZOOM = 3.0
    private const val MAX_ZOOM = 16.0
    private const val EDGE_PADDING_FACTOR = 0.75

    fun calculate(
        coordinates: List<RouteCoordinate>,
        viewportMinDimensionPixels: Double
    ): RouteCameraViewport? {
        if (coordinates.isEmpty() || viewportMinDimensionPixels <= 0.0) {
            return null
        }

        val minLatitude = coordinates.minOf { it.latitude }
        val maxLatitude = coordinates.maxOf { it.latitude }
        val minLongitude = coordinates.minOf { it.longitude }
        val maxLongitude = coordinates.maxOf { it.longitude }
        val centerLatitude = (minLatitude + maxLatitude) / 2.0
        val centerLongitude = (minLongitude + maxLongitude) / 2.0

        if (coordinates.size == 1) {
            return RouteCameraViewport(centerLatitude, centerLongitude, MAX_ZOOM)
        }

        val latitudeSpan = maxLatitude - minLatitude
        val longitudeSpan = (maxLongitude - minLongitude) *
            cos(Math.toRadians(centerLatitude))
        val maxNormalizedSpan = max(latitudeSpan, longitudeSpan) / 360.0
        if (maxNormalizedSpan <= 0.0) {
            return RouteCameraViewport(centerLatitude, centerLongitude, MAX_ZOOM)
        }

        val zoom = ln(
            viewportMinDimensionPixels * EDGE_PADDING_FACTOR /
                (TILE_SIZE_PIXELS * maxNormalizedSpan)
        ) / ln(2.0)

        return RouteCameraViewport(
            latitude = centerLatitude,
            longitude = centerLongitude,
            zoom = zoom.coerceIn(MIN_ZOOM, MAX_ZOOM)
        )
    }
}
