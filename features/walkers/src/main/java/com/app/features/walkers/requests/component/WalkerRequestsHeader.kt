
package com.app.features.walkers.requests.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.app.core.ui.components.SectionHeader
import com.app.core.ui.components.TextActionButton

@Composable
fun WalkerRequestsHeader(
    requestCount: Int,
    isLoading: Boolean,
    isProcessing: Boolean,
    onRefresh: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SectionHeader(
            title = "Solicitudes de paseo",
            subtitle = when {
                isLoading ->
                    "Actualizando solicitudes..."

                requestCount == 0 ->
                    "Administrá las solicitudes que recibís."

                requestCount == 1 ->
                    "Tenés 1 solicitud registrada."

                else ->
                    "Tenés $requestCount solicitudes registradas."
            },
            modifier = Modifier.weight(1f)
        )

        TextActionButton(
            text = if (isLoading) "Actualizando..." else "Actualizar",
            onClick = onRefresh,
            enabled = !isLoading && !isProcessing
        )
    }
}
