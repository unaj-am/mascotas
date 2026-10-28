
package com.app.features.walkers.requests

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.app.core.ui.components.AppTextField
import com.app.core.ui.components.EmptyContent
import com.app.core.ui.components.LoadingContent
import com.app.core.ui.components.PrimaryActionButton
import com.app.core.ui.components.SuccessDialog
import com.app.core.ui.theme.DimensUI

@Composable
fun WalkRequestFormScreen(
    uiState: WalkRequestFormUiState,
    onPetSelected: (String) -> Unit,
    onScheduledAtChange: (String) -> Unit,
    onDurationChange: (String) -> Unit,
    onObservationsChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {

    if (uiState.isSuccess) {
        SuccessDialog(
            title = "Solicitud enviada",
            message = "El paseador recibirá tu solicitud para aceptarla o rechazarla.",
            onConfirm = onSuccess
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = DimensUI.ScreenHorizontalPadding,
                vertical = DimensUI.ScreenVerticalPadding
            ),
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SpaceLarge
        )
    ) {

        TextButton(onClick = onBack) {
            Text("← Volver")
        }

        Text(
            text = "Solicitar paseo",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Completá los datos para enviarle una solicitud al paseador.",
            style = MaterialTheme.typography.bodyMedium
        )

        if (uiState.isLoading) {
            LoadingContent()
        } else {

            Text(
                text = "Seleccioná tus mascotas",
                style = MaterialTheme.typography.titleMedium
            )

            if (uiState.pets.isEmpty()) {
                EmptyContent(
                    message = "No tenés mascotas registradas."
                )
            } else {
                uiState.pets.forEach { pet ->

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = pet.id in uiState.selectedPetIds,
                            onCheckedChange = {
                                onPetSelected(pet.id)
                            }
                        )

                        Text(
                            text = pet.name,
                            modifier = Modifier.padding(
                                top = DimensUI.SpaceMedium
                            )
                        )
                    }
                }
            }

            AppTextField(
                value = uiState.scheduledAt,
                onValueChange = onScheduledAtChange,
                label = "Fecha y hora",
                placeholder = "2026-11-01T15:00",
                modifier = Modifier.fillMaxWidth()
            )

            AppTextField(
                value = uiState.durationMinutes,
                onValueChange = onDurationChange,
                label = "Duración en minutos",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.fillMaxWidth()
            )

            AppTextField(
                value = uiState.observations,
                onValueChange = onObservationsChange,
                label = "Observaciones",
                singleLine = false,
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            PrimaryActionButton(
                text = "Enviar solicitud",
                onClick = onSubmit,
                enabled = uiState.selectedPetIds.isNotEmpty()
                        && !uiState.isSubmitting,
                isLoading = uiState.isSubmitting,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
