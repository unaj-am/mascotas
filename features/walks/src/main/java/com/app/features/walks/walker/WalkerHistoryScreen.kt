package com.app.features.walks.walker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.core.domain.model.WalkStatus
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
                horizontal = 20.dp,
                vertical = 16.dp
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        WalkerWalksHeader(
            title = "Historial de paseos",
            subtitle = "Consultá los paseos finalizados y cancelados.",
            onRefresh = onRefresh
        )

        WalkerWalksFeedback(
            operationMessage = state.operationMessage,
            operationError = state.operationError,
            loadingError = state.errorMessage,
            onRetry = onRefresh
        )

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
            modifier = Modifier.weight(1f)
        )
    }
}