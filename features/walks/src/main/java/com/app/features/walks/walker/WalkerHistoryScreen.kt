
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
fun WalkerHistoryScreen(
    state: WalkerWalksUiState,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    val history = state.walks
        .filter { walk ->
            walk.status == WalkStatus.COMPLETED ||
                    walk.status == WalkStatus.CANCELLED
        }
        .sortedByDescending { walk ->
            walk.scheduledAt
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
                title = "Historial de paseos",
                subtitle = "Consultá los paseos finalizados y cancelados.",
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
                displayedWalks = history,
                loadingMessage = "Cargando historial...",
                emptyTitle = "Sin paseos en el historial",
                emptyMessage = "Los paseos completados o cancelados aparecerán acá.",
                onRetry = onRetry,
                onStart = {},
                onComplete = {},
                showActions = false,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview(name = "Historial - Finalizado y cancelado", showBackground = true, widthDp = 411, heightDp = 891)
@Composable
private fun WalkerHistoryScreenPreview() {
    val completed = Walk(
                id = "WALK001",
                ownerId = "OWNER001",
                walkerId = "WALKER001",
                petIds = listOf("PET001", "PET002"),
                scheduledAt = "2026-11-20T10:00:00",
                durationMinutes = 45,
                observations = "Llevar agua y evitar calles muy transitadas."
            ).copy(
        status = WalkStatus.COMPLETED,
        startedAt = "2026-11-20T13:00:00Z",
        finishedAt = "2026-11-20T13:45:00Z",
        distanceKm = 2.3
    )
    val cancelled = completed.copy(id = "WALK002", status = WalkStatus.CANCELLED)
    WalkerHistoryPreviewContent(
        WalkerWalksUiState(
            walks = listOf(completed, cancelled),
            walkDetails = mapOf(
                completed.id to WalkerWalkDetails("Franco Gómez", listOf("Luna", "Toby")),
                cancelled.id to WalkerWalkDetails("Ana López", listOf("Milo"))
            ),
            isLoading = false
        )
    )
}

@Preview(name = "Historial - Vacío", showBackground = true, widthDp = 411, heightDp = 891)
@Composable
private fun WalkerHistoryEmptyPreview() {
    WalkerHistoryPreviewContent(WalkerWalksUiState(isLoading = false))
}

@Preview(name = "Historial - Error", showBackground = true, widthDp = 411, heightDp = 891)
@Composable
private fun WalkerHistoryErrorPreview() {
    WalkerHistoryPreviewContent(
        WalkerWalksUiState(isLoading = false, errorMessage = "No se pudo cargar el historial.")
    )
}

@Composable
private fun WalkerHistoryPreviewContent(state: WalkerWalksUiState) {
    MascotasThemeUI(isWalker = true) {
        WalkerHistoryScreen(state = state, onRetry = {}, onBack = {})
    }
}

