package com.app.features.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.app.core.ui.theme.DimensUI

@Composable
fun WalkerAvailabilityCard(
    available: Boolean,
    isUpdating: Boolean,
    onAvailabilityChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DimensUI.SpaceMedium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(
                    DimensUI.SpaceXSmall
                )
            ) {
                Text(
                    text = "Mi disponibilidad",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = if (available) {
                        "Disponible para recibir solicitudes"
                    } else {
                        "No disponible para nuevos paseos"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (isUpdating) {
                    Text(
                        text = "Actualizando disponibilidad...",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Switch(
                checked = available,
                onCheckedChange = onAvailabilityChange,
                enabled = !isUpdating
            )
        }
    }
}