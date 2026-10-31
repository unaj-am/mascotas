
package com.app.features.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.app.core.domain.model.WalkRequest
import com.app.core.ui.components.AppCard
import com.app.core.ui.components.InfoRow
import com.app.core.ui.components.StatusBadge
import com.app.core.ui.theme.DimensUI

@Composable
fun PendingRequestPreviewCard(
    request: WalkRequest,
    modifier: Modifier = Modifier
) {
    val petCount = request.petIds.size

    AppCard(
        modifier = modifier,
        contentPadding = DimensUI.CardPaddingLarge
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                DimensUI.SpaceLarge
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Nueva solicitud",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                StatusBadge(
                    text = "Pendiente",
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            InfoRow(
                label = "Fecha y hora",
                value = request.scheduledAt,
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(
                            DimensUI.IconSizeMedium
                        ),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )

            InfoRow(
                label = "Duración",
                value = "${request.durationMinutes} minutos",
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(
                            DimensUI.IconSizeMedium
                        ),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )

            InfoRow(
                label = "Mascotas",
                value = if (petCount == 1) {
                    "1 mascota"
                } else {
                    "$petCount mascotas"
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Pets,
                        contentDescription = null,
                        modifier = Modifier.size(
                            DimensUI.IconSizeMedium
                        ),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )

            if (request.observations.isNotBlank()) {
                InfoRow(
                    label = "Observaciones",
                    value = request.observations,
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Notes,
                            contentDescription = null,
                            modifier = Modifier.size(
                                DimensUI.IconSizeMedium
                            ),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )
            }
        }
    }
}
