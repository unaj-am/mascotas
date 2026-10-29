package com.app.features.walks.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.core.domain.model.RequestStatus
import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkRequest
import com.app.core.domain.model.WalkStatus
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun WalkCard(
    walk: Walk,
    ownerName: String? = null,
    petNames: List<String> = emptyList(),
    isProcessing: Boolean = false,
    showStart: Boolean = false,
    showComplete: Boolean = false,
    showTrack: Boolean = false,
    showCancel: Boolean = false,
    onStart: () -> Unit = {},
    onComplete: () -> Unit = {},
    onTrack: () -> Unit = {},
    onCancel: () -> Unit = {},
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Paseo",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = statusLabel(walk.status),
                    style = MaterialTheme.typography.labelLarge,
                    color = statusColor(walk.status)
                )
            }

            if (!ownerName.isNullOrBlank()) {
                Text(
                    text = "Dueño: $ownerName",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = formatScheduledAt(walk.scheduledAt),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Duración: ${walk.durationMinutes} minutos",
                style = MaterialTheme.typography.bodyMedium
            )

            val displayedPetNames = petNames
                .filter { it.isNotBlank() }

            if (displayedPetNames.isNotEmpty()) {
                Text(
                    text = "Mascotas: ${displayedPetNames.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Text(
                    text = "Mascotas: ${walk.petIds.size}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (walk.observations.isNotBlank()) {
                Text(
                    text = "Observaciones: ${walk.observations}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (showStart) {
                    TextButton(
                        onClick = onStart,
                        enabled = !isProcessing
                    ) {
                        Text(
                            if (isProcessing) {
                                "Procesando…"
                            } else {
                                "Iniciar paseo"
                            }
                        )
                    }
                }

                if (showComplete) {
                    TextButton(
                        onClick = onComplete,
                        enabled = !isProcessing
                    ) {
                        Text(
                            if (isProcessing) {
                                "Procesando…"
                            } else {
                                "Finalizar paseo"
                            }
                        )
                    }
                }

                if (showTrack) {
                    TextButton(onClick = onTrack) {
                        Text("Seguimiento")
                    }
                }

                if (showCancel) {
                    TextButton(
                        onClick = onCancel,
                        enabled = !isProcessing
                    ) {
                        Text("Cancelar")
                    }
                }
            }
        }
    }
}

@Composable
fun WalkRequestCard(
    request: WalkRequest,
    isProcessing: Boolean = false,
    onCancel: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Solicitud de paseo",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = requestStatusLabel(request.status),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (request.status == RequestStatus.REJECTED) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            }

            if (request.ownerName.isNotBlank()) {
                Text(
                    text = "Dueño: ${request.ownerName}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = formatScheduledAt(request.scheduledAt),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Duración: ${request.durationMinutes} minutos",
                style = MaterialTheme.typography.bodyMedium
            )

            if (request.petNames.isNotEmpty()) {
                Text(
                    text = "Mascotas: ${request.petNames.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Text(
                    text = "Mascotas: ${request.petIds.size}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (request.observations.isNotBlank()) {
                Text(
                    text = "Observaciones: ${request.observations}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (request.status == RequestStatus.PENDING) {
                TextButton(
                    onClick = onCancel,
                    enabled = !isProcessing
                ) {
                    Text(
                        if (isProcessing) {
                            "Procesando…"
                        } else {
                            "Cancelar solicitud"
                        }
                    )
                }
            }
        }
    }
}

private fun statusLabel(status: WalkStatus): String =
    when (status) {
        WalkStatus.SCHEDULED -> "Confirmado"
        WalkStatus.IN_PROGRESS -> "En curso"
        WalkStatus.COMPLETED -> "Finalizado"
        WalkStatus.CANCELLED -> "Cancelado"
    }

private fun requestStatusLabel(status: RequestStatus): String =
    when (status) {
        RequestStatus.PENDING -> "Pendiente"
        RequestStatus.ACCEPTED -> "Aceptada"
        RequestStatus.REJECTED -> "Rechazada"
        RequestStatus.CANCELLED -> "Cancelada"
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

private fun formatScheduledAt(value: String): String {
    if (value.isBlank()) {
        return "Fecha sin especificar"
    }

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