
package com.app.features.tracking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkLocation
import com.app.core.domain.model.WalkStatus
import com.app.core.ui.components.AppCard
import com.app.core.ui.components.DomainErrorMessage
import com.app.core.ui.components.InfoRow
import com.app.core.ui.components.LoadingContent
import com.app.core.ui.components.SectionHeader
import com.app.core.ui.components.StatusBadge
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
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import com.app.core.domain.model.MatchedRoute
private const val OPEN_FREE_MAP_STYLE =
    "https://tiles.openfreemap.org/styles/liberty"

private const val MAX_ROUTE_ACCURACY_METERS = 35f
private const val MIN_ROUTE_DISTANCE_METERS = 4.0
private const val MAX_ROUTE_DISTANCE_METERS = 10.0
private const val EARTH_RADIUS_METERS = 6_371_000.0

@Composable
fun WalkTrackingScreen(
    walkId: String,
    onBack: () -> Unit,
    viewModel: WalkTrackingViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(walkId) {
        viewModel.load(
            walkId = walkId,
            forceRefresh = true
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionHeader(
            title = "Seguimiento del paseo",
            subtitle = "Consultá el estado y los datos del servicio.",
            actionText = "Volver",
            onAction = onBack
        )

        if (state.isLoading && state.walk == null) {
            LoadingContent(
                modifier = Modifier.weight(1f),
                message = "Consultando el paseo..."
            )
        } else {
            state.errorMessage?.let { message ->
                DomainErrorMessage(
                    message = message,
                    onRetry = viewModel::refresh
                )
            }

            val walk = state.walk

            if (walk != null) {
                WalkTrackingContent(
                    walk = walk,
                    currentLocation = state.currentLocation,
                    locationHistory = state.locationHistory,
                    matchedRoute = state.matchedRoute,
                    locationErrorMessage = state.locationErrorMessage,
                    historyErrorMessage = state.historyErrorMessage,
                    onRefresh = viewModel::refresh
                )
            } else if (state.errorMessage == null) {
                Text(
                    text = "No hay información disponible para este paseo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun WalkTrackingContent(
    walk: Walk,
    currentLocation: WalkLocation?,
    locationHistory: List<WalkLocation>,
    matchedRoute: MatchedRoute?,
    locationErrorMessage: String?,
    historyErrorMessage: String?,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (
            walk.status == WalkStatus.IN_PROGRESS ||
            walk.status == WalkStatus.COMPLETED ||
            walk.status == WalkStatus.CANCELLED
        ) {
            WalkTrackingMap(
                walkId = walk.id,
                currentLocation = currentLocation,
                locationHistory = locationHistory,
                matchedRoute = matchedRoute
            )
        }

        AppCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatusBadge(
                    text = statusLabel(walk.status),
                    containerColor = statusBackgroundColor(walk.status),
                    contentColor = statusTextColor(walk.status)
                )

                InfoRow(
                    label = "Fecha programada",
                    value = formatDate(walk.scheduledAt)
                )

                InfoRow(
                    label = "Duración prevista",
                    value = "${walk.durationMinutes} minutos"
                )

                InfoRow(
                    label = "Cantidad de mascotas",
                    value = walk.petIds.size.toString()
                )

                walk.startedAt?.let { startedAt ->
                    InfoRow(
                        label = "Inicio real",
                        value = formatDate(startedAt)
                    )
                }

                walk.finishedAt?.let { finishedAt ->
                    InfoRow(
                        label = "Finalización real",
                        value = formatDate(finishedAt)
                    )
                }

                if (walk.observations.isNotBlank()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Observaciones",
                            style = MaterialTheme.typography.titleSmall
                        )

                        Text(
                            text = walk.observations,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        when (walk.status) {
            WalkStatus.IN_PROGRESS -> {
                AppCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Paseo en curso",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = when {
                                currentLocation != null ->
                                    "Se está recibiendo la ubicación del paseador."

                                locationErrorMessage != null ->
                                    "Hay un problema al recibir la ubicación."

                                else ->
                                    "Esperando la primera ubicación del paseador..."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        locationErrorMessage?.let { message ->
                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        historyErrorMessage?.let { message ->
                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        TextButton(onClick = onRefresh) {
                            Text("Actualizar estado")
                        }
                    }
                }
            }

            WalkStatus.SCHEDULED -> {
                AppCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "El paseo está confirmado. El seguimiento estará disponible cuando comience.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            WalkStatus.COMPLETED -> {
                AppCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "El paseo finalizó. Podés consultar el recorrido y los horarios registrados.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            WalkStatus.CANCELLED -> {
                AppCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Este paseo fue cancelado.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun WalkTrackingMap(
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

    val routeSegments: List<List<Position>> =
        if (matchedSegments.isNotEmpty()) {
            matchedSegments
        } else {
            val gpsPositions = locationHistory.map { location ->
                Position(
                    latitude = location.latitude,
                    longitude = location.longitude
                )
            }

            if (gpsPositions.size >= 2) {
                listOf(gpsPositions)
            } else {
                emptyList()
            }
        }

    val isMatchedRoute = matchedSegments.isNotEmpty()

    AppCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                    // Dibujamos cada segmento por separado.
                    // Esto evita conectar artificialmente tramos
                    // que OSRM identificó como recorridos diferentes.
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

                    // El marcador siempre representa la ubicación
                    // GPS actual recibida desde Firestore.
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

/**
 * Prepara los puntos que se utilizarán para dibujar el recorrido.
 *
 * No modifica el historial original recibido desde Firestore.
 */
private fun filterRouteLocations(
    history: List<WalkLocation>,
    currentLocation: WalkLocation?
): List<WalkLocation> {

    // Combinar historial y ubicación actual.
    // El timestamp evita dibujar dos veces el mismo punto.
    val allLocations = (history + listOfNotNull(currentLocation))
        .distinctBy { it.recordedAt }
        .sortedBy { it.recordedAt }

    // Descartar puntos con precisión insuficiente.
    val validLocations = allLocations.filter { location ->
        location.accuracy <= MAX_ROUTE_ACCURACY_METERS
    }

    if (validLocations.size <= 1) {
        return validLocations
    }

    val filteredLocations = mutableListOf<WalkLocation>()

    // El primer punto válido se conserva.
    filteredLocations.add(validLocations.first())

    for (index in 1 until validLocations.size) {
        val candidate = validLocations[index]
        val previous = filteredLocations.last()

        val distance = calculateDistanceMeters(
            first = previous,
            second = candidate
        )

        // Adaptar el umbral a la precisión de los dos puntos.
        val accuracyThreshold = maxOf(
            previous.accuracy,
            candidate.accuracy
        ) * 0.35

        val minimumDistance = accuracyThreshold.coerceIn(
            MIN_ROUTE_DISTANCE_METERS,
            MAX_ROUTE_DISTANCE_METERS
        )

        // Evitar puntos demasiado cercanos.
        if (distance >= minimumDistance) {
            filteredLocations.add(candidate)
        }
    }

    return filteredLocations
}

/**
 * Calcula la distancia geográfica entre dos coordenadas
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
        sqrt(a),
        sqrt(1 - a)
    )

    return EARTH_RADIUS_METERS * c
}

@Composable
private fun statusBackgroundColor(status: WalkStatus) =
    when (status) {
        WalkStatus.SCHEDULED ->
            MaterialTheme.colorScheme.primaryContainer

        WalkStatus.IN_PROGRESS ->
            MaterialTheme.colorScheme.tertiaryContainer

        WalkStatus.COMPLETED ->
            MaterialTheme.colorScheme.secondaryContainer

        WalkStatus.CANCELLED ->
            MaterialTheme.colorScheme.errorContainer
    }

@Composable
private fun statusTextColor(status: WalkStatus) =
    when (status) {
        WalkStatus.SCHEDULED ->
            MaterialTheme.colorScheme.onPrimaryContainer

        WalkStatus.IN_PROGRESS ->
            MaterialTheme.colorScheme.onTertiaryContainer

        WalkStatus.COMPLETED ->
            MaterialTheme.colorScheme.onSecondaryContainer

        WalkStatus.CANCELLED ->
            MaterialTheme.colorScheme.onErrorContainer
    }

private fun statusLabel(status: WalkStatus): String =
    when (status) {
        WalkStatus.SCHEDULED -> "Paseo confirmado"
        WalkStatus.IN_PROGRESS -> "Paseo en curso"
        WalkStatus.COMPLETED -> "Paseo finalizado"
        WalkStatus.CANCELLED -> "Paseo cancelado"
    }

private fun formatDate(value: String): String {
    if (value.isBlank()) return "Sin especificar"

    return runCatching {
        val dateTime = runCatching {
            LocalDateTime.parse(value)
        }.getOrElse {
            LocalDateTime.ofInstant(
                Instant.parse(value),
                ZoneId.systemDefault()
            )
        }

        dateTime.format(
            DateTimeFormatter.ofPattern(
                "dd/MM/yyyy 'a las' HH:mm",
                Locale("es", "AR")
            )
        )
    }.getOrDefault(value)
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
