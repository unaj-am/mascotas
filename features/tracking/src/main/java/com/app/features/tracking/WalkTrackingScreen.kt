
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkStatus
import com.app.core.ui.components.AppCard
import com.app.core.ui.components.DomainErrorMessage
import com.app.core.ui.components.InfoRow
import com.app.core.ui.components.LoadingContent
import com.app.core.ui.components.SectionHeader
import com.app.core.ui.components.StatusBadge
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.GestureOptions
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.Position
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val OPEN_FREE_MAP_STYLE =
    "https://tiles.openfreemap.org/styles/liberty"

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
            if (state.errorMessage != null) {
                DomainErrorMessage(
                    message = state.errorMessage.orEmpty(),
                    onRetry = viewModel::refresh
                )
            }

            val walk = state.walk

            if (walk != null) {
                WalkTrackingContent(
                    walk = walk,
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
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Mapa del seguimiento
        if (walk.status == WalkStatus.IN_PROGRESS) {
            WalkTrackingMap()
        }

        // Información del paseo
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
                            text = "El mapa está disponible. La ubicación del paseador y el recorrido en tiempo real se incorporarán mediante GPS y Firestore.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

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
                        text = "El paseo finalizó. Podés consultar los horarios registrados.",
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

/**
 * Mapa base de seguimiento.
 */
@Composable
private fun WalkTrackingMap() {

    val cameraState = rememberCameraState(
        firstPosition = CameraPosition(
            target = Position(
                latitude = -34.6037,
                longitude = -58.3816
            ),
            zoom = 12.0
        )
    )

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
                )
            }

            Text(
                text = "Vista inicial de referencia. El mapa se centrará en la ubicación real del paseador cuando conectemos el GPS.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
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
