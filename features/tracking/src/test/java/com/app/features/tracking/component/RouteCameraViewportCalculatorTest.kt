package com.app.features.tracking.component

import com.app.core.domain.model.RouteCoordinate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RouteCameraViewportCalculatorTest {

    @Test
    fun calculate_centersViewportOnRouteBounds() {
        val viewport = RouteCameraViewportCalculator.calculate(
            coordinates = listOf(
                RouteCoordinate(-34.60, -58.40),
                RouteCoordinate(-34.64, -58.36)
            ),
            viewportMinDimensionPixels = 320.0
        )

        assertNotNull(viewport)
        assertEquals(-34.62, viewport!!.latitude, 0.000001)
        assertEquals(-58.38, viewport.longitude, 0.000001)
    }

    @Test
    fun calculate_zoomsOutAsRouteExtentGrows() {
        val shortRoute = listOf(
            RouteCoordinate(-34.60, -58.40),
            RouteCoordinate(-34.601, -58.399)
        )
        val longRoute = listOf(
            RouteCoordinate(-34.60, -58.40),
            RouteCoordinate(-34.70, -58.30)
        )

        val shortViewport = RouteCameraViewportCalculator.calculate(shortRoute, 320.0)!!
        val longViewport = RouteCameraViewportCalculator.calculate(longRoute, 320.0)!!

        assertTrue(longViewport.zoom < shortViewport.zoom)
    }

    @Test
    fun calculate_returnsNullForNoCoordinatesOrNoViewport() {
        assertNull(RouteCameraViewportCalculator.calculate(emptyList(), 320.0))
        assertNull(
            RouteCameraViewportCalculator.calculate(
                listOf(RouteCoordinate(0.0, 0.0)),
                0.0
            )
        )
    }
}
