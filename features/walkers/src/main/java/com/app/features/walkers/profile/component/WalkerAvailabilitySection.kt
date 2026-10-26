
package com.app.features.walkers.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.app.core.ui.theme.DimensUI

@Composable
fun WalkerAvailabilitySection(
    available: Boolean,
    isSaving: Boolean,
    onAvailableChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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
                text = "Disponibilidad",
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
        }

        Switch(
            checked = available,
            onCheckedChange = onAvailableChange,
            enabled = !isSaving
        )
    }
}
