
package com.app.features.home.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.ui.components.AppCard
import com.app.core.ui.components.AppSwitchRow

@Composable
fun WalkerAvailabilityCard(
    available: Boolean,
    isUpdating: Boolean,
    onAvailabilityChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier.fillMaxWidth()
    ) {
        AppSwitchRow(
            checked = available,
            onCheckedChange = onAvailabilityChange,
            title = "Mi disponibilidad",
            description = if (isUpdating) {
                "Actualizando disponibilidad..."
            } else if (available) {
                "Disponible para recibir solicitudes"
            } else {
                "No disponible para nuevos paseos"
            },
            enabled = !isUpdating
        )
    }
}
