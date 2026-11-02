
package com.app.features.tracking.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.core.domain.model.MatchedRoute
import com.app.core.domain.model.WalkLocation
import com.app.core.ui.components.AppCard
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.map.GestureOptions
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.LineString
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Position
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val OPEN_FREE_MAP_STYLE =
    "https://tiles.openfreemap.org/styles/liberty"

@Composable
fun WalkTrackingMap(
    walkId: String,
    currentLocation: WalkLocation?,
    locationHistory: List<WalkLocation>,
    matchedRoute: MatchedRoute?
) {
    val cameraState = rememberCameraState(
        firstPosition = CameraPosition(
            target = Position(
                latitude = -34.6037,
                longitude = -58.3816
            ),
            zoom = 12.0
        )
    )

    LaunchedEffect(
        currentLocation?.recordedAt,
        locationHistory.lastOrNull()?.recordedAt
    ) {
        val targetLocation = currentLocation
            ?: locationHistory.lastOrNull()

        targetLocation?.let { location ->
            cameraState.position = CameraPosition(
                target = Position(
                    latitude = location.latitude,
                    longitude = location.longitude
                ),
                zoom = 16.0
            )
        }
    }

    // Si OSRM devuelve geometría, utilizamos sus segmentos.
    // Si todavía no hay resultado, mostramos el historial GPS.

    // Segmentos devueltos por OSRM.
    val matchedSegments: List<List<Position>> =
        matchedRoute?.segments
            ?.map { segment ->
                segment.coordinates.map { coordinate ->
                    Position(
                        latitude = coordinate.latitude,
                        longitude = coordinate.longitude
                    )
                }
            }
            ?.filter { segment -> segment.size >= 2 }
            .orEmpty()

    // Procesamos el historial GPS para utilizarlo como alternativa
    // cuando todavía no existe una geometría ajustada por OSRM.
    val filteredGpsLocations = WalkRouteProcessor.filterLocations(
        history = locationHistory,
        currentLocation = currentLocation
    )

    val gpsPositions = filteredGpsLocations.map { location ->
        Position(
            latitude = location.latitude,
            longitude = location.longitude
        )
    }

    val routeSegments: List<List<Position>> =
        if (matchedSegments.isNotEmpty()) {
            matchedSegments
        } else if (gpsPositions.size >= 2) {
            listOf(gpsPositions)
        } else {
            emptyList()
        }

    val isMatchedRoute = matchedSegments.isNotEmpty()

    AppCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Mapa del recorrido",
                style = MaterialTheme.typography.titleMedium
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                MaplibreMap(
                    modifier = Modifier.fillMaxSize(),
                    baseStyle = BaseStyle.Uri(
                        OPEN_FREE_MAP_STYLE
                    ),
                    cameraState = cameraState,
                    options = MapOptions(
                        gestureOptions = GestureOptions.Standard,
                        ornamentOptions = OrnamentOptions.OnlyLogo
                    )
                ) {
                    // Cada segmento se dibuja por separado para evitar
                    // conectar artificialmente tramos diferentes.
                    routeSegments.forEachIndexed { index, positions ->

                        val routeSource = rememberGeoJsonSource(
                            data = GeoJsonData.Features(
                                LineString(positions)
                            )
                        )

                        LineLayer(
                            id = "walk-route-layer-$walkId-$index",
                            source = routeSource,
                            color = const(
                                if (isMatchedRoute) {
                                    Color(0xFF2E7D32)
                                } else {
                                    Color(0xFF1565C0)
                                }
                            ),
                            width = const(5.dp)
                        )
                    }

                    // El marcador representa la ubicación GPS actual
                    // recibida desde Firestore.
                    currentLocation?.let { location ->

                        val locationSource = rememberGeoJsonSource(
                            data = GeoJsonData.Features(
                                Point(
                                    Position(
                                        latitude = location.latitude,
                                        longitude = location.longitude
                                    )
                                )
                            )
                        )

                        CircleLayer(
                            id = "walk-location-layer-$walkId",
                            source = locationSource,
                            radius = const(10.dp),
                            color = const(Color(0xFF1976D2)),
                            strokeColor = const(Color.White),
                            strokeWidth = const(3.dp)
                        )
                    }
                }
            }

            Text(
                text = when {
                    currentLocation != null ->
                        "Última ubicación: ${formatTimestamp(currentLocation.recordedAt)}"

                    locationHistory.isNotEmpty() ->
                        "Se recibieron ${locationHistory.size} posiciones."

                    else ->
                        "Esperando la primera ubicación del paseador..."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = if (isMatchedRoute) {
                    "Recorrido ajustado a las calles mediante OSRM."
                } else {
                    "Mostrando recorrido GPS original."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatTimestamp(value: Long): String {
    return runCatching {
        Instant.ofEpochMilli(value)
            .atZone(ZoneId.systemDefault())
            .format(
                DateTimeFormatter.ofPattern(
                    "HH:mm:ss",
                    Locale("es", "AR")
                )
            )
    }.getOrDefault("No disponible")
}
