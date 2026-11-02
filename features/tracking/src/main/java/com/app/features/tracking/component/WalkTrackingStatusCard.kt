
package com.app.features.tracking.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.core.domain.model.WalkLocation
import com.app.core.domain.model.WalkStatus
import com.app.core.ui.components.AppCard

@Composable
fun WalkTrackingStatusCard(
    status: WalkStatus,
    currentLocation: WalkLocation?,
    locationErrorMessage: String?,
    historyErrorMessage: String?,
    onRefresh: () -> Unit
) {
    AppCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when (status) {
                WalkStatus.IN_PROGRESS -> {
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

                    TextButton(
                        onClick = onRefresh
                    ) {
                        Text("Actualizar estado")
                    }
                }

                WalkStatus.SCHEDULED -> {
                    Text(
                        text = "El paseo está confirmado. El seguimiento estará disponible cuando comience.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                WalkStatus.COMPLETED -> {
                    Text(
                        text = "El paseo finalizó. Podés consultar el recorrido y los horarios registrados.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                WalkStatus.CANCELLED -> {
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
