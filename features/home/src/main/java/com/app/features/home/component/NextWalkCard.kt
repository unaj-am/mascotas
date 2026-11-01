
package com.app.features.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.app.core.ui.components.AppCard
import com.app.core.ui.components.InfoRow
import com.app.core.ui.components.PrimaryActionButton
import com.app.core.ui.components.SectionHeader
import com.app.core.ui.components.StatusBadge
import com.app.core.ui.theme.DimensUI
import androidx.compose.ui.unit.dp
@Composable
fun NextWalkCard(
    petName: String?,
    petBreed: String?,
    walkDate: String?,
    onFindWalkerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasScheduledWalk = !petName.isNullOrBlank()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SpaceMedium
        )
    ) {
        SectionHeader(
            title = "Próximo paseo",
            subtitle = "Organizá las próximas aventuras de tu mascota"
        )

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
                    Surface(
                        shape = RoundedCornerShape(12),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier.size(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (hasScheduledWalk) {
                                    Icons.Filled.WatchLater
                                } else {
                                    Icons.Filled.Pets
                                },
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    if (hasScheduledWalk) {
                        StatusBadge(
                            text = "Programado",
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                if (hasScheduledWalk) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(
                            DimensUI.SpaceXSmall
                        )
                    ) {
                        Text(
                            text = petName.orEmpty(),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (!petBreed.isNullOrBlank()) {
                            Text(
                                text = petBreed,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (!walkDate.isNullOrBlank()) {
                        InfoRow(
                            label = "Fecha y horario",
                            value = walkDate,
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Filled.CalendarMonth,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        )
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(
                            DimensUI.SpaceXSmall
                        )
                    ) {
                        Text(
                            text = "Todavía no tenés paseos programados",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Encontrá un paseador de confianza y organizá el próximo paseo de tu mascota.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                PrimaryActionButton(
                    text = if (hasScheduledWalk) {
                        "Buscar otro paseador"
                    } else {
                        "Buscar paseador"
                    },
                    onClick = onFindWalkerClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
