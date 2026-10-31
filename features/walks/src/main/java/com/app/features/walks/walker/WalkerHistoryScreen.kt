
package com.app.features.walks.walker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.domain.model.WalkStatus
import com.app.core.ui.components.AppAnimatedVisibility
import com.app.core.ui.theme.DimensUI
import com.app.features.walks.walker.component.WalkerWalksContent
import com.app.features.walks.walker.component.WalkerWalksFeedback
import com.app.features.walks.walker.component.WalkerWalksHeader

@Composable
fun WalkerHistoryScreen(
    state: WalkerWalksUiState,
    onRefresh: () -> Unit
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
                onRefresh = onRefresh
            )
        }

        WalkerWalksFeedback(
            operationMessage = state.operationMessage,
            operationError = state.operationError,
            loadingError = state.errorMessage,
            onRetry = onRefresh
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
                onRefresh = onRefresh,
                onStart = {},
                onComplete = {},
                showActions = false,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
