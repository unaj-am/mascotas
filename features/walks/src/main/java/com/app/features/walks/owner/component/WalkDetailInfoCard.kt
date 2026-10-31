
package com.app.features.walks.owner.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.domain.model.Walk
import com.app.core.ui.components.AppCard
import com.app.core.ui.components.InfoRow
import com.app.core.ui.components.StatusBadge
import com.app.core.ui.theme.DimensUI
import com.app.features.walks.owner.util.formatWalkDate
import com.app.features.walks.owner.util.walkStatusBackgroundColor
import com.app.features.walks.owner.util.walkStatusLabel
import com.app.features.walks.owner.util.walkStatusTextColor
import java.util.Locale

@Composable
fun WalkDetailInfoCard(
    walk: Walk,
    walkerName: String?,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier.fillMaxWidth(),
        contentPadding = DimensUI.CardPaddingLarge
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                DimensUI.SpaceLarge
            )
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    DimensUI.SpaceSmall
                )
            ) {
                Text(
                    text = "Información general",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                StatusBadge(
                    text = walkStatusLabel(walk.status),
                    containerColor = walkStatusBackgroundColor(walk.status),
                    contentColor = walkStatusTextColor(walk.status)
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(
                    DimensUI.SpaceMedium
                )
            ) {
                InfoRow(
                    label = "Fecha programada",
                    value = formatWalkDate(walk.scheduledAt)
                )

                InfoRow(
                    label = "Duración prevista",
                    value = "${walk.durationMinutes} minutos"
                )

                InfoRow(
                    label = "Mascotas",
                    value = if (walk.petIds.size == 1) {
                        "1 mascota"
                    } else {
                        "${walk.petIds.size} mascotas"
                    }
                )

                InfoRow(
                    label = "Paseador",
                    value = walkerName
                        ?.takeIf { it.isNotBlank() }
                        ?: "Nombre no disponible"
                )
            }

            if (
                walk.startedAt != null ||
                walk.finishedAt != null ||
                walk.distanceKm != null
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(
                        DimensUI.SpaceMedium
                    )
                ) {
                    Text(
                        text = "Datos del recorrido",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    walk.startedAt?.let { startedAt ->
                        InfoRow(
                            label = "Inicio real",
                            value = formatWalkDate(startedAt)
                        )
                    }

                    walk.finishedAt?.let { finishedAt ->
                        InfoRow(
                            label = "Finalización real",
                            value = formatWalkDate(finishedAt)
                        )
                    }

                    walk.distanceKm?.let { distance ->
                        InfoRow(
                            label = "Distancia recorrida",
                            value = String.format(
                                Locale("es", "AR"),
                                "%.2f km",
                                distance
                            )
                        )
                    }
                }
            }

            if (walk.observations.isNotBlank()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(
                        DimensUI.SpaceSmall
                    )
                ) {
                    Text(
                        text = "Observaciones",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
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
}
