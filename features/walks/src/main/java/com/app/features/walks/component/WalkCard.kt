
package com.app.features.walks.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.app.core.domain.model.RequestStatus
import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkRequest
import com.app.core.domain.model.WalkStatus
import com.app.core.ui.components.AppCard
import com.app.core.ui.components.InfoRow
import com.app.core.ui.components.OutlinedActionButton
import com.app.core.ui.components.PrimaryActionButton
import com.app.core.ui.components.StatusBadge
import com.app.core.ui.components.TextActionButton
import com.app.core.ui.theme.DimensUI
import com.app.features.walks.util.formatWalkDate
import com.app.features.walks.util.walkStatusBackgroundColor
import com.app.features.walks.util.walkStatusShortLabel
import com.app.features.walks.util.walkStatusTextColor

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
        onClick = onClick,
        contentPadding = DimensUI.CardPaddingLarge
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                DimensUI.SpaceLarge
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Paseo",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                StatusBadge(
                    text = walkStatusShortLabel(walk.status),
                    containerColor = walkStatusBackgroundColor(walk.status),
                    contentColor = walkStatusTextColor(walk.status)
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(
                    DimensUI.SpaceMedium
                )
            ) {
                if (!ownerName.isNullOrBlank()) {
                    InfoRow(
                        label = "Dueño",
                        value = ownerName
                    )
                }

                InfoRow(
                    label = "Fecha",
                    value = formatWalkDate(
                        value = walk.scheduledAt,
                        emptyValue = "Fecha sin especificar"
                    )
                )

                InfoRow(
                    label = "Duración",
                    value = "${walk.durationMinutes} minutos"
                )

                val displayedPetNames = petNames.filter(String::isNotBlank)

                InfoRow(
                    label = "Mascotas",
                    value = displayedPetNames
                        .takeIf { it.isNotEmpty() }
                        ?.joinToString(", ")
                        ?: walk.petIds.size.toString()
                )

                if (walk.observations.isNotBlank()) {
                    InfoRow(
                        label = "Observaciones",
                        value = walk.observations
                    )
                }
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
        modifier = Modifier.fillMaxWidth(),
        contentPadding = DimensUI.CardPaddingLarge
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                DimensUI.SpaceLarge
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Solicitud de paseo",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                StatusBadge(
                    text = requestStatusLabel(request.status),
                    containerColor = requestStatusBackgroundColor(request.status),
                    contentColor = requestStatusTextColor(request.status)
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(
                    DimensUI.SpaceMedium
                )
            ) {
                if (request.ownerName.isNotBlank()) {
                    InfoRow(
                        label = "Dueño",
                        value = request.ownerName
                    )
                }

                InfoRow(
                    label = "Fecha",
                    value = formatWalkDate(
                        value = request.scheduledAt,
                        emptyValue = "Fecha sin especificar"
                    )
                )

                InfoRow(
                    label = "Duración",
                    value = "${request.durationMinutes} minutos"
                )

                InfoRow(
                    label = "Mascotas",
                    value = request.petNames
                        .filter(String::isNotBlank)
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
            }

            if (request.status == RequestStatus.PENDING) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextActionButton(
                        text = if (isProcessing) {
                            "Procesando…"
                        } else {
                            "Cancelar solicitud"
                        },
                        onClick = onCancel,
                        enabled = !isProcessing
                    )
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
    if (!showStart && !showComplete && !showTrack && !showCancel) return

    Column(
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SpaceSmall
        )
    ) {
        when {
            showStart -> {
                PrimaryActionButton(
                    text = if (isProcessing) {
                        "Procesando…"
                    } else {
                        "Iniciar paseo"
                    },
                    onClick = onStart,
                    enabled = !isProcessing,
                    isLoading = isProcessing
                )
            }

            showComplete -> {
                PrimaryActionButton(
                    text = if (isProcessing) {
                        "Procesando…"
                    } else {
                        "Finalizar paseo"
                    },
                    onClick = onComplete,
                    enabled = !isProcessing,
                    isLoading = isProcessing
                )
            }
        }

        if (showTrack) {
            OutlinedActionButton(
                text = "Seguimiento",
                onClick = onTrack
            )
        }

        if (showCancel) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextActionButton(
                    text = if (isProcessing) {
                        "Procesando…"
                    } else {
                        "Cancelar"
                    },
                    onClick = onCancel,
                    enabled = !isProcessing
                )
            }
        }
    }
}

private fun requestStatusLabel(status: RequestStatus): String =
    when (status) {
        RequestStatus.PENDING -> "Pendiente"
        RequestStatus.ACCEPTED -> "Aceptada"
        RequestStatus.REJECTED -> "Rechazada"
        RequestStatus.CANCELLED -> "Cancelada"
    }

@Composable
private fun requestStatusBackgroundColor(status: RequestStatus) =
    when (status) {
        RequestStatus.PENDING ->
            MaterialTheme.colorScheme.tertiaryContainer

        RequestStatus.ACCEPTED ->
            MaterialTheme.colorScheme.primaryContainer

        RequestStatus.REJECTED ->
            MaterialTheme.colorScheme.errorContainer

        RequestStatus.CANCELLED ->
            MaterialTheme.colorScheme.surfaceVariant
    }

@Composable
private fun requestStatusTextColor(status: RequestStatus) =
    when (status) {
        RequestStatus.PENDING ->
            MaterialTheme.colorScheme.onTertiaryContainer

        RequestStatus.ACCEPTED ->
            MaterialTheme.colorScheme.onPrimaryContainer

        RequestStatus.REJECTED ->
            MaterialTheme.colorScheme.onErrorContainer

        RequestStatus.CANCELLED ->
            MaterialTheme.colorScheme.onSurfaceVariant
    }
