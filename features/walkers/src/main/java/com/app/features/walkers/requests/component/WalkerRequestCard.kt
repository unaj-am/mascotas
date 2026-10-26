
package com.app.features.walkers.requests.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.domain.model.RequestStatus
import com.app.core.domain.model.WalkRequest
import com.app.core.ui.theme.DimensUI

@Composable
fun WalkerRequestCard(
    request: WalkRequest,
    isProcessing: Boolean,
    isAnyRequestProcessing: Boolean,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(DimensUI.SpaceLarge),
            verticalArrangement = Arrangement.spacedBy(
                DimensUI.SpaceMedium
            )
        ) {
            Text(
                text = "Solicitud ${request.id}",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Dueño: ${request.ownerId}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Fecha y hora: ${request.scheduledAt}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Duración: ${request.durationMinutes} minutos",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Mascotas: ${request.petIds.joinToString()}",
                style = MaterialTheme.typography.bodyMedium
            )

            if (request.observations.isNotBlank()) {
                Text(
                    text = "Observaciones: ${request.observations}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = "Estado: ${request.status.toDisplayName()}",
                style = MaterialTheme.typography.labelLarge,
                color = when (request.status) {
                    RequestStatus.PENDING ->
                        MaterialTheme.colorScheme.primary

                    RequestStatus.ACCEPTED ->
                        MaterialTheme.colorScheme.tertiary

                    RequestStatus.REJECTED,
                    RequestStatus.CANCELLED ->
                        MaterialTheme.colorScheme.onSurfaceVariant
                }
            )

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

private fun RequestStatus.toDisplayName(): String =
    when (this) {
        RequestStatus.PENDING -> "Pendiente"
        RequestStatus.ACCEPTED -> "Aceptada"
        RequestStatus.REJECTED -> "Rechazada"
        RequestStatus.CANCELLED -> "Cancelada"
    }
