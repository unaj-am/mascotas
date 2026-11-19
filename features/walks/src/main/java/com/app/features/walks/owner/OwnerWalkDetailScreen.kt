package com.app.features.walks.owner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.app.core.ui.theme.MascotasThemeUI
import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkStatus
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.core.ui.components.DomainErrorMessage
import com.app.core.ui.components.LoadingContent
import com.app.features.walks.owner.component.OwnerWalkDetailContent
import com.app.features.walks.owner.component.OwnerWalkDetailHeader

@Composable
fun OwnerWalkDetailScreen(
    state: OwnerWalkDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onTrackWalk: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .widthIn(max = 880.dp)
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OwnerWalkDetailHeader(
            onBack = onBack
        )

        when {
            state.isLoading && state.walk == null -> {
                LoadingContent(
                    modifier = Modifier.weight(1f),
                    message = "Cargando detalle del paseo..."
                )
            }

            state.errorMessage != null && state.walk == null -> {
                DomainErrorMessage(
                    message = state.errorMessage.orEmpty(),
                    onRetry = onRetry
                )
            }

            state.walk != null -> {
                OwnerWalkDetailContent(
                    walk = state.walk,
                    walkerName = state.walkerName,
                    onTrackWalk = onTrackWalk,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Preview(name = "Detalle - En curso", showBackground = true, widthDp = 411, heightDp = 891)
@Composable
private fun OwnerWalkDetailScreenPreview() {
    OwnerWalkDetailPreviewContent(
        OwnerWalkDetailUiState(
            walk = Walk(
                id = "WALK001",
                ownerId = "OWNER001",
                walkerId = "WALKER001",
                petIds = listOf("PET001", "PET002"),
                scheduledAt = "2026-11-20T10:00:00",
                durationMinutes = 45,
                observations = "Llevar agua y evitar calles muy transitadas."
            ).copy(
                status = WalkStatus.IN_PROGRESS,
                startedAt = "2026-11-20T13:00:00Z"
            ),
            walkerName = "Mateo Pérez",
            isLoading = false
        )
    )
}

@Preview(name = "Detalle - Cargando", showBackground = true, widthDp = 411, heightDp = 891)
@Composable
private fun OwnerWalkDetailLoadingPreview() {
    OwnerWalkDetailPreviewContent(OwnerWalkDetailUiState())
}

@Preview(name = "Detalle - Error", showBackground = true, widthDp = 411, heightDp = 891)
@Composable
private fun OwnerWalkDetailErrorPreview() {
    OwnerWalkDetailPreviewContent(
        OwnerWalkDetailUiState(isLoading = false, errorMessage = "No se pudo cargar el paseo.")
    )
}

@Composable
private fun OwnerWalkDetailPreviewContent(state: OwnerWalkDetailUiState) {
    MascotasThemeUI {
        OwnerWalkDetailScreen(state = state, onBack = {}, onRetry = {}, onTrackWalk = {})
    }
}
