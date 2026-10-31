
package com.app.features.walkers.requestform

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.ui.components.AppAnimatedVisibility
import com.app.core.ui.components.LoadingContent
import com.app.core.ui.components.SectionHeader
import com.app.core.ui.components.SuccessDialog
import com.app.core.ui.components.TextActionButton
import com.app.core.ui.theme.DimensUI
import com.app.features.walkers.requestform.component.WalkRequestDetailsSection
import com.app.features.walkers.requestform.component.WalkRequestPetsSection
import com.app.features.walkers.requestform.component.WalkRequestSubmitSection

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
        TextActionButton(
            text = "← Volver",
            onClick = onBack,
            enabled = !uiState.isSubmitting
        )

        AppAnimatedVisibility(
            visible = true,
            durationMillis = 260
        ) {
            SectionHeader(
                title = "Solicitar paseo",
                subtitle = "Completá los datos para enviarle una solicitud al paseador."
            )
        }

        if (uiState.isLoading) {
            LoadingContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = DimensUI.SpaceXLarge),
                message = "Cargando tus mascotas..."
            )
        } else {
            AppAnimatedVisibility(
                visible = true,
                durationMillis = 280
            ) {
                WalkRequestPetsSection(
                    pets = uiState.pets,
                    selectedPetIds = uiState.selectedPetIds,
                    isSubmitting = uiState.isSubmitting,
                    onPetSelected = onPetSelected
                )
            }

            AppAnimatedVisibility(
                visible = true,
                durationMillis = 320
            ) {
                WalkRequestDetailsSection(
                    scheduledAt = uiState.scheduledAt,
                    durationMinutes = uiState.durationMinutes,
                    observations = uiState.observations,
                    isSubmitting = uiState.isSubmitting,
                    onScheduledAtChange = onScheduledAtChange,
                    onDurationChange = onDurationChange,
                    onObservationsChange = onObservationsChange
                )
            }

            AppAnimatedVisibility(
                visible = true,
                durationMillis = 360
            ) {
                WalkRequestSubmitSection(
                    errorMessage = uiState.errorMessage,
                    hasSelectedPets = uiState.selectedPetIds.isNotEmpty(),
                    isSubmitting = uiState.isSubmitting,
                    onSubmit = onSubmit
                )
            }
        }
    }
}
