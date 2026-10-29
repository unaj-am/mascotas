
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.app.core.ui.components.AppCard
import com.app.core.ui.components.AppTextField
import com.app.core.ui.components.DomainErrorMessage
import com.app.core.ui.components.EmptyContent
import com.app.core.ui.components.LoadingContent
import com.app.core.ui.components.PrimaryActionButton
import com.app.core.ui.components.SectionHeader
import com.app.core.ui.components.StatusBadge
import com.app.core.ui.components.SuccessDialog
import com.app.core.ui.theme.DimensUI

/**
 * Formulario para solicitar un paseo.
 *
 * La pantalla presenta las mascotas del dueño,
 * los datos del paseo y el estado de la operación.
 *
 * La lógica de selección, validación y envío permanece
 * en el ViewModel.
 */
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
            DimensUI.SectionSpacing
        )
    ) {
        TextButton(
            onClick = onBack,
            enabled = !uiState.isSubmitting
        ) {
            Text("← Volver")
        }

        SectionHeader(
            title = "Solicitar paseo",
            subtitle = "Completá los datos para enviarle una solicitud al paseador."
        )

        if (uiState.isLoading) {
            LoadingContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = DimensUI.SpaceXLarge),
                message = "Cargando tus mascotas..."
            )
        } else {
            AppCard(
                modifier = Modifier.fillMaxWidth()
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
                        Text(
                            text = "Tus mascotas",
                            style = MaterialTheme.typography.titleMedium
                        )

                        StatusBadge(
                            text = if (
                                uiState.selectedPetIds.size == 1
                            ) {
                                "1 seleccionada"
                            } else {
                                "${uiState.selectedPetIds.size} seleccionadas"
                            }
                        )
                    }

                    if (uiState.pets.isEmpty()) {
                        EmptyContent(
                            title = "No tenés mascotas registradas",
                            message = "Necesitás registrar al menos una mascota antes de solicitar un paseo.",
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = "Seleccioná una o más mascotas para el paseo.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        uiState.pets.forEach { pet ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = pet.id in uiState.selectedPetIds,
                                    onCheckedChange = {
                                        onPetSelected(pet.id)
                                    },
                                    enabled = !uiState.isSubmitting
                                )

                                Text(
                                    text = pet.name,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(
                                            start = DimensUI.SpaceSmall
                                        ),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            AppCard(
                modifier = Modifier.fillMaxWidth()
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
                        value = uiState.scheduledAt,
                        onValueChange = onScheduledAtChange,
                        label = "Fecha y hora",
                        placeholder = "2026-11-01T15:00",
                        enabled = !uiState.isSubmitting
                    )

                    Text(
                        text = "Formato: AAAA-MM-DDTHH:MM",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    AppTextField(
                        value = uiState.durationMinutes,
                        onValueChange = { value ->
                            if (value.all(Char::isDigit)) {
                                onDurationChange(value)
                            }
                        },
                        label = "Duración en minutos",
                        placeholder = "60",
                        keyboardType = KeyboardType.Number,
                        enabled = !uiState.isSubmitting
                    )

                    AppTextField(
                        value = uiState.observations,
                        onValueChange = onObservationsChange,
                        label = "Observaciones",
                        placeholder = "Indicá cuidados o información relevante para el paseo.",
                        singleLine = false,
                        enabled = !uiState.isSubmitting
                    )
                }
            }

            uiState.errorMessage?.let { message ->
                DomainErrorMessage(
                    message = message,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            PrimaryActionButton(
                text = "Enviar solicitud",
                onClick = onSubmit,
                enabled = uiState.selectedPetIds.isNotEmpty() &&
                        !uiState.isSubmitting,
                isLoading = uiState.isSubmitting
            )
        }
    }
}
