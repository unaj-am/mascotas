
package com.app.features.walks.owner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkStatus
import com.app.core.ui.components.DomainErrorMessage
import com.app.core.ui.components.LoadingContent
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun OwnerWalkDetailScreen(
    walkId: String,
    onBack: () -> Unit,
    onTrackWalk: (String) -> Unit,
    viewModel: OwnerWalkDetailViewModel = hiltViewModel()
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Detalle del paseo",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )

            OutlinedButton(onClick = onBack) {
                Text("Volver")
            }
        }

        when {
            state.isLoading && state.walk == null -> {
                LoadingContent(
                    modifier = Modifier.weight(1f)
                )
            }

            state.errorMessage != null && state.walk == null -> {
                DomainErrorMessage(
                    message = state.errorMessage.orEmpty(),
                    onRetry = viewModel::refresh
                )
            }

            state.walk != null -> {
                WalkDetailContent(
                    walk = state.walk!!,
                    walkerName = state.walkerName,
                    onTrackWalk = onTrackWalk
                )
            }
        }
    }
}

@Composable
private fun WalkDetailContent(
    walk: Walk,
    walkerName: String?,
    onTrackWalk: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.surfaceContainerLow
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = statusLabel(walk.status),
                    style = MaterialTheme.typography.titleLarge,
                    color = statusColor(walk.status)
                )

                DetailRow(
                    label = "Fecha programada",
                    value = formatDate(walk.scheduledAt)
                )

                DetailRow(
                    label = "Duración prevista",
                    value = "${walk.durationMinutes} minutos"
                )

                DetailRow(
                    label = "Mascotas",
                    value = walk.petIds.size.toString()
                )

                DetailRow(
                    label = "Paseador",
                    value = walkerName
                        ?.takeIf { it.isNotBlank() }
                        ?: "Cargando nombre del paseador..."
                )

                walk.startedAt?.let {
                    DetailRow(
                        label = "Inicio real",
                        value = formatDate(it)
                    )
                }

                walk.finishedAt?.let {
                    DetailRow(
                        label = "Finalización real",
                        value = formatDate(it)
                    )
                }

                walk.distanceKm?.let {
                    DetailRow(
                        label = "Distancia recorrida",
                        value = "%.2f km".format(
                            Locale("es", "AR"),
                            it
                        )
                    )
                }

                if (walk.observations.isNotBlank()) {
                    Text(
                        text = "Observaciones",
                        style = MaterialTheme.typography.titleSmall
                    )

                    Text(
                        text = walk.observations,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        if (walk.status == WalkStatus.IN_PROGRESS) {
            Button(
                onClick = { onTrackWalk(walk.id) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Consultar seguimiento")
            }
        }

        if (walk.status == WalkStatus.SCHEDULED) {
            Text(
                text = "El paseo está confirmado. El seguimiento estará disponible cuando comience.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (walk.status == WalkStatus.COMPLETED) {
            Text(
                text = "Este paseo ya finalizó.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun statusColor(status: WalkStatus) =
    when (status) {
        WalkStatus.SCHEDULED ->
            MaterialTheme.colorScheme.primary

        WalkStatus.IN_PROGRESS ->
            MaterialTheme.colorScheme.tertiary

        WalkStatus.COMPLETED ->
            MaterialTheme.colorScheme.secondary

        WalkStatus.CANCELLED ->
            MaterialTheme.colorScheme.error
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
