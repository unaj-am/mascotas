
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
import com.app.core.domain.model.WalkRequest
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.core.ui.components.AppAnimatedVisibility
import com.app.core.ui.theme.DimensUI
import com.app.features.walks.owner.component.OwnerWalksContent
import com.app.features.walks.owner.component.OwnerWalksFeedback
import com.app.features.walks.owner.component.OwnerWalksFilterBar
import com.app.features.walks.owner.component.OwnerWalksHeader

@Composable
fun OwnerWalksScreen(
    state: OwnerWalksUiState,
    onRetry: () -> Unit,
    onTrackWalk: (String) -> Unit,
    onOpenWalk: (String) -> Unit,
    onCancelRequest: (String) -> Unit,
    onCancelWalk: (String) -> Unit,
    onFilterSelected: (OwnerWalkFilter) -> Unit
) {
    Column(
        modifier = Modifier
            .widthIn(max = 1120.dp)
            .fillMaxSize()
            .padding(
                horizontal = DimensUI.ScreenHorizontalPadding,
                vertical = DimensUI.ScreenTopPadding
            ),
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SectionSpacingCompact
        )
    ) {
        AppAnimatedVisibility(visible = true) {
            OwnerWalksHeader()
        }

        OwnerWalksFeedback(
            operationMessage = state.operationMessage,
            operationError = state.operationError,
            loadingError = state.errorMessage,
            onRetry = onRetry
        )

        AppAnimatedVisibility(
            visible = state.walks.isNotEmpty() ||
                    state.requests.isNotEmpty()
        ) {
            OwnerWalksFilterBar(
                walks = state.walks,
                requests = state.requests,
                selectedFilter = state.selectedFilter,
                onFilterSelected = onFilterSelected
            )
        }

        AppAnimatedVisibility(
            visible = true,
            modifier = Modifier.weight(1f)
        ) {
            OwnerWalksContent(
                state = state,
                onTrackWalk = onTrackWalk,
                onOpenWalk = onOpenWalk,
                onCancelRequest = onCancelRequest,
                onCancelWalk = onCancelWalk,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview(name = "Mis paseos - Con datos", showBackground = true, widthDp = 411, heightDp = 891)
@Composable
private fun OwnerWalksScreenPreview() {
    OwnerWalksPreviewContent(
        OwnerWalksUiState(
            isLoading = false,
            walks = listOf(Walk(
                id = "WALK001",
                ownerId = "OWNER001",
                walkerId = "WALKER001",
                petIds = listOf("PET001", "PET002"),
                scheduledAt = "2026-11-20T10:00:00",
                durationMinutes = 45,
                observations = "Llevar agua y evitar calles muy transitadas."
            )),
            requests = listOf(
                WalkRequest(
                    id = "REQUEST001",
                    ownerId = "OWNER001",
                    walkerId = "WALKER002",
                    petIds = listOf("PET001"),
                    scheduledAt = "2026-11-21T15:00:00",
                    durationMinutes = 30,
                    observations = "Paseo tranquilo por la plaza."
                )
            )
        )
    )
}

@Preview(name = "Mis paseos - Vacío", showBackground = true, widthDp = 411, heightDp = 891)
@Composable
private fun OwnerWalksEmptyPreview() {
    OwnerWalksPreviewContent(OwnerWalksUiState(isLoading = false))
}

@Preview(name = "Mis paseos - Cargando", showBackground = true, widthDp = 411, heightDp = 891)
@Composable
private fun OwnerWalksLoadingPreview() {
    OwnerWalksPreviewContent(OwnerWalksUiState())
}

@Preview(name = "Mis paseos - Error", showBackground = true, widthDp = 411, heightDp = 891)
@Composable
private fun OwnerWalksErrorPreview() {
    OwnerWalksPreviewContent(
        OwnerWalksUiState(isLoading = false, errorMessage = "No se pudieron cargar tus paseos.")
    )
}

@Composable
private fun OwnerWalksPreviewContent(state: OwnerWalksUiState) {
    MascotasThemeUI {
        OwnerWalksScreen(
            state = state,
            onRetry = {},
            onTrackWalk = {},
            onOpenWalk = {},
            onCancelRequest = {},
            onCancelWalk = {},
            onFilterSelected = {}
        )
    }
}

