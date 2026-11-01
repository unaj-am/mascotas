
package com.app.features.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.app.core.ui.components.AppCard
import com.app.core.ui.components.AppSwitchRow
import com.app.core.ui.components.StatusBadge
import com.app.core.ui.theme.DimensUI

@Composable
fun WalkerAvailabilityCard(
    available: Boolean,
    isUpdating: Boolean,
    onAvailabilityChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier.fillMaxWidth(),
        contentPadding = DimensUI.CardPaddingLarge
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                DimensUI.SpaceMedium
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tu disponibilidad",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                StatusBadge(
                    text = when {
                        isUpdating -> "Actualizando"
                        available -> "Disponible"
                        else -> "No disponible"
                    },
                    containerColor = when {
                        isUpdating -> MaterialTheme.colorScheme.tertiaryContainer
                        available -> MaterialTheme.colorScheme.secondaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    contentColor = when {
                        isUpdating -> MaterialTheme.colorScheme.onTertiaryContainer
                        available -> MaterialTheme.colorScheme.onSecondaryContainer
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            AppSwitchRow(
                checked = available,
                onCheckedChange = onAvailabilityChange,
                title = if (available) {
                    "Estoy disponible"
                } else {
                    "Estoy desconectado"
                },
                description = when {
                    isUpdating -> "Actualizando tu disponibilidad..."
                    available -> "Podés recibir nuevas solicitudes de paseo."
                    else -> "No recibirás nuevas solicitudes mientras estés desconectado."
                },
                enabled = !isUpdating
            )
        }
    }
}
