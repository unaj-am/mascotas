package com.app.features.walkers.requestform.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.app.core.ui.components.AppCard
import com.app.core.ui.components.AppTextField
import com.app.core.ui.theme.DimensUI

@Composable
fun WalkRequestDetailsSection(
    scheduledAt: String,
    durationMinutes: String,
    observations: String,
    isSubmitting: Boolean,
    onScheduledAtChange: (String) -> Unit,
    onDurationChange: (String) -> Unit,
    onObservationsChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                DimensUI.SpaceLarge
            )
        ) {
            Text(
                text = "Datos del paseo",
                style = MaterialTheme.typography.titleMedium
            )

            AppTextField(
                value = scheduledAt,
                onValueChange = onScheduledAtChange,
                label = "Fecha y hora",
                placeholder = "2026-11-01T15:00",
                enabled = !isSubmitting
            )

            Text(
                text = "Formato: AAAA-MM-DDTHH:MM",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            AppTextField(
                value = durationMinutes,
                onValueChange = { value ->
                    if (value.all(Char::isDigit)) {
                        onDurationChange(value)
                    }
                },
                label = "Duración en minutos",
                placeholder = "60",
                keyboardType = KeyboardType.Number,
                enabled = !isSubmitting
            )

            AppTextField(
                value = observations,
                onValueChange = onObservationsChange,
                label = "Observaciones",
                placeholder = "Indicá cuidados o información relevante para el paseo.",
                singleLine = false,
                enabled = !isSubmitting
            )
        }
    }
}