
package com.app.features.walkers.requests.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.domain.model.RequestStatus
import com.app.core.domain.model.WalkRequest
import com.app.core.ui.components.AppCard
import com.app.core.ui.components.InfoRow
import com.app.core.ui.components.StatusBadge
import com.app.core.ui.theme.DimensUI

/**
 * Tarjeta de solicitud recibida por un paseador.
 *
 * La presentación utiliza componentes de core:ui.
 * Las operaciones de aceptar y rechazar se delegan
 * a los callbacks recibidos.
 */
@Composable
fun WalkerRequestCard(
    request: WalkRequest,
    isProcessing: Boolean,
    isAnyRequestProcessing: Boolean,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier,
    ownerName: String = "Dueño",
    petNames: List<String> = emptyList()
) {
    val statusText = when (request.status) {
        RequestStatus.PENDING -> "Pendiente"
        RequestStatus.ACCEPTED -> "Aceptada"
        RequestStatus.REJECTED -> "Rechazada"
        RequestStatus.CANCELLED -> "Cancelada"
    }

    val statusContainerColor = when (request.status) {
        RequestStatus.PENDING ->
            MaterialTheme.colorScheme.primaryContainer

        RequestStatus.ACCEPTED ->
            MaterialTheme.colorScheme.tertiaryContainer

        RequestStatus.REJECTED,
        RequestStatus.CANCELLED ->
            MaterialTheme.colorScheme.surfaceVariant
    }

    val statusContentColor = when (request.status) {
        RequestStatus.PENDING ->
            MaterialTheme.colorScheme.onPrimaryContainer

        RequestStatus.ACCEPTED ->
            MaterialTheme.colorScheme.onTertiaryContainer

        RequestStatus.REJECTED,
        RequestStatus.CANCELLED ->
            MaterialTheme.colorScheme.onSurfaceVariant
    }

    val displayedPetNames = when {
        petNames.isNotEmpty() ->
            petNames.joinToString(", ")

        request.petIds.size == 1 ->
            "1 mascota"

        else ->
            "${request.petIds.size} mascotas"
    }

    AppCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                DimensUI.SpaceLarge
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Solicitud de paseo",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                StatusBadge(
                    text = statusText,
                    containerColor = statusContainerColor,
                    contentColor = statusContentColor
                )
            }

            InfoRow(
                label = "Dueño",
                value = ownerName
            )

            InfoRow(
                label = "Fecha y hora",
                value = request.scheduledAt
            )

            InfoRow(
                label = "Duración",
                value = "${request.durationMinutes} minutos"
            )

            InfoRow(
                label = "Mascotas",
                value = displayedPetNames
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
                    horizontalArrangement = Arrangement.spacedBy(
                        DimensUI.SpaceSmall
                    )
                ) {
                    Button(
                        onClick = onAccept,
                        enabled = !isAnyRequestProcessing,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (isProcessing) {
                                "Procesando..."
                            } else {
                                "Aceptar"
                            }
                        )
                    }

                    OutlinedButton(
                        onClick = onReject,
                        enabled = !isAnyRequestProcessing,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Rechazar")
                    }
                }
            }
        }
    }
}
