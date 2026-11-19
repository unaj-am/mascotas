
package com.app.features.walks.walker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.app.core.ui.theme.MascotasThemeUI
import com.app.core.domain.model.Walk
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.core.domain.model.WalkStatus
import com.app.core.ui.components.AppAnimatedVisibility
import com.app.core.ui.theme.DimensUI
import com.app.features.walks.walker.component.WalkerWalksContent
import com.app.features.walks.walker.component.WalkerWalksFeedback
import com.app.features.walks.walker.component.WalkerWalksHeader

@Composable
fun WalkerActiveWalkScreen(
    state: WalkerWalksUiState,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    onStart: (String) -> Unit,
    onComplete: (String) -> Unit
) {
    val actionableWalks = state.walks.filter { walk ->
        walk.status == WalkStatus.SCHEDULED ||
                walk.status == WalkStatus.IN_PROGRESS
    }

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
            WalkerWalksHeader(
                title = "Paseos activos",
                subtitle = "Iniciá los paseos confirmados y finalizá los que estén en curso.",
                actionText = "Volver",
                onAction = onBack
            )
        }

        WalkerWalksFeedback(
            operationMessage = state.operationMessage,
            operationError = state.operationError,
            loadingError = state.errorMessage.takeIf {
                state.walks.isNotEmpty()
            },
            onRetry = onRetry
        )

        AppAnimatedVisibility(
            visible = true,
            modifier = Modifier.weight(1f)
        ) {
            WalkerWalksContent(
                state = state,
                displayedWalks = actionableWalks,
                loadingMessage = "Cargando tus paseos...",
                emptyTitle = "Sin paseos activos",
                emptyMessage = "No tenés paseos confirmados pendientes de iniciar ni paseos en curso.",
                onRetry = onRetry,
                onStart = onStart,
                onComplete = onComplete,
                showActions = true,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview(name = "Paseos activos - En curso", showBackground = true, widthDp = 411, heightDp = 891)
@Composable
private fun WalkerActiveWalkScreenPreview() {
    val walk = Walk(
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
    )
    WalkerActiveWalkPreviewContent(
        WalkerWalksUiState(
            walks = listOf(walk),
            walkDetails = mapOf(walk.id to WalkerWalkDetails("Franco Gómez", listOf("Luna", "Toby"))),
            isLoading = false
        )
    )
}

@Preview(name = "Paseos activos - Vacío", showBackground = true, widthDp = 411, heightDp = 891)
@Composable
private fun WalkerActiveWalkEmptyPreview() {
    WalkerActiveWalkPreviewContent(WalkerWalksUiState(isLoading = false))
}

@Preview(name = "Paseos activos - Cargando", showBackground = true, widthDp = 411, heightDp = 891)
@Composable
private fun WalkerActiveWalkLoadingPreview() {
    WalkerActiveWalkPreviewContent(WalkerWalksUiState())
}

@Preview(name = "Paseos activos - Error", showBackground = true, widthDp = 411, heightDp = 891)
@Composable
private fun WalkerActiveWalkErrorPreview() {
    WalkerActiveWalkPreviewContent(
        WalkerWalksUiState(isLoading = false, errorMessage = "No se pudieron cargar los paseos.")
    )
}

@Composable
private fun WalkerActiveWalkPreviewContent(state: WalkerWalksUiState) {
    MascotasThemeUI(isWalker = true) {
        WalkerActiveWalkScreen(
            state = state,
            onRetry = {},
            onBack = {},
            onStart = {},
            onComplete = {}
        )
    }
}

