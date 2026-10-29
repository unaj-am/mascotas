
package com.app.features.walks.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.domain.model.RequestStatus
import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkRequest
import com.app.core.domain.model.WalkStatus
import com.app.core.ui.components.AppCard
import com.app.core.ui.components.InfoRow
import com.app.core.ui.components.StatusBadge
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.app.core.ui.theme.DimensUI
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
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                DimensUI.SpaceMedium
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Paseo",
                    style = MaterialTheme.typography.titleMedium
                )

                StatusBadge(
                    text = statusLabel(walk.status),
                    containerColor = statusBackgroundColor(walk.status),
                    contentColor = statusTextColor(walk.status)
                )
            }

            if (!ownerName.isNullOrBlank()) {
                InfoRow(
                    label = "Dueño",
                    value = ownerName
                )
            }

            InfoRow(
                label = "Fecha",
                value = formatScheduledAt(walk.scheduledAt)
            )

            InfoRow(
                label = "Duración",
                value = "${walk.durationMinutes} minutos"
            )

            val displayedPetNames = petNames.filter {
                it.isNotBlank()
            }

            InfoRow(
                label = "Mascotas",
                value = if (displayedPetNames.isNotEmpty()) {
                    displayedPetNames.joinToString(", ")
                } else {
                    walk.petIds.size.toString()
                }
            )

            if (walk.observations.isNotBlank()) {
                InfoRow(
                    label = "Observaciones",
                    value = walk.observations
                )
            }

            WalkActions(
                isProcessing = isProcessing,
                showStart = showStart,
                showComplete = showComplete,
                showTrack = showTrack,
                showCancel = showCancel,
                onStart = onStart,
                onComplete = onComplete,
                onTrack = onTrack,
                onCancel = onCancel
            )
        }
    }
}

@Composable
fun WalkRequestCard(
    request: WalkRequest,
    isProcessing: Boolean = false,
    onCancel: () -> Unit = {}
) {
    AppCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                DimensUI.SpaceMedium
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Solicitud de paseo",
                    style = MaterialTheme.typography.titleMedium
                )

                StatusBadge(
                    text = requestStatusLabel(request.status),
                    containerColor = requestStatusBackgroundColor(request.status),
                    contentColor = requestStatusTextColor(request.status)
                )
            }

            if (request.ownerName.isNotBlank()) {
                InfoRow(
                    label = "Dueño",
                    value = request.ownerName
                )
            }

            InfoRow(
                label = "Fecha",
                value = formatScheduledAt(request.scheduledAt)
            )

            InfoRow(
                label = "Duración",
                value = "${request.durationMinutes} minutos"
            )

            InfoRow(
                label = "Mascotas",
                value = request.petNames
                    .filter { it.isNotBlank() }
                    .takeIf { it.isNotEmpty() }
                    ?.joinToString(", ")
                    ?: request.petIds.size.toString()
            )

            if (request.observations.isNotBlank()) {
                InfoRow(
                    label = "Observaciones",
                    value = request.observations
                )
            }

            if (request.status == RequestStatus.PENDING) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onCancel,
                        enabled = !isProcessing
                    ) {
                        Text(
                            text = if (isProcessing) {
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
}

@Composable
private fun WalkActions(
    isProcessing: Boolean,
    showStart: Boolean,
    showComplete: Boolean,
    showTrack: Boolean,
    showCancel: Boolean,
    onStart: () -> Unit,
    onComplete: () -> Unit,
    onTrack: () -> Unit,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        if (showStart) {
            TextButton(
                onClick = onStart,
                enabled = !isProcessing
            ) {
                Text(
                    if (isProcessing) "Procesando…" else "Iniciar paseo"
                )
            }
        }

        if (showComplete) {
            TextButton(
                onClick = onComplete,
                enabled = !isProcessing
            ) {
                Text(
                    if (isProcessing) "Procesando…" else "Finalizar paseo"
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
private fun statusBackgroundColor(status: WalkStatus) =
    when (status) {
        WalkStatus.SCHEDULED -> MaterialTheme.colorScheme.primaryContainer
        WalkStatus.IN_PROGRESS -> MaterialTheme.colorScheme.tertiaryContainer
        WalkStatus.COMPLETED -> MaterialTheme.colorScheme.secondaryContainer
        WalkStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer
    }

@Composable
private fun statusTextColor(status: WalkStatus) =
    when (status) {
        WalkStatus.SCHEDULED -> MaterialTheme.colorScheme.onPrimaryContainer
        WalkStatus.IN_PROGRESS -> MaterialTheme.colorScheme.onTertiaryContainer
        WalkStatus.COMPLETED -> MaterialTheme.colorScheme.onSecondaryContainer
        WalkStatus.CANCELLED -> MaterialTheme.colorScheme.onErrorContainer
    }

@Composable
private fun requestStatusBackgroundColor(status: RequestStatus) =
    when (status) {
        RequestStatus.PENDING -> MaterialTheme.colorScheme.tertiaryContainer
        RequestStatus.ACCEPTED -> MaterialTheme.colorScheme.primaryContainer
        RequestStatus.REJECTED -> MaterialTheme.colorScheme.errorContainer
        RequestStatus.CANCELLED -> MaterialTheme.colorScheme.surfaceVariant
    }

@Composable
private fun requestStatusTextColor(status: RequestStatus) =
    when (status) {
        RequestStatus.PENDING -> MaterialTheme.colorScheme.onTertiaryContainer
        RequestStatus.ACCEPTED -> MaterialTheme.colorScheme.onPrimaryContainer
        RequestStatus.REJECTED -> MaterialTheme.colorScheme.onErrorContainer
        RequestStatus.CANCELLED -> MaterialTheme.colorScheme.onSurfaceVariant
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
