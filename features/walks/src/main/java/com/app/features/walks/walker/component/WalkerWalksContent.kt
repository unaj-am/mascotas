
package com.app.features.walks.walker.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.domain.model.Walk
import com.app.core.ui.components.EmptyContent
import com.app.core.ui.components.ErrorContent
import com.app.core.ui.components.LoadingContent
import com.app.features.walks.walker.WalkerWalksUiState

@Composable
fun WalkerWalksContent(
    state: WalkerWalksUiState,
    displayedWalks: List<Walk>,
    loadingMessage: String,
    emptyTitle: String,
    emptyMessage: String,
    onRefresh: () -> Unit,
    onStart: (String) -> Unit,
    onComplete: (String) -> Unit,
    showActions: Boolean,
    modifier: Modifier = Modifier
) {
    when {
        state.isLoading && state.walks.isEmpty() -> {
            LoadingContent(
                modifier = modifier,
                message = loadingMessage
            )
        }

        state.errorMessage != null && state.walks.isEmpty() -> {
            ErrorContent(
                message = state.errorMessage,
                onRetry = onRefresh,
                modifier = modifier
            )
        }

        displayedWalks.isEmpty() -> {
            EmptyContent(
                title = emptyTitle,
                message = emptyMessage,
                modifier = modifier,
                actionText = "Actualizar",
                onAction = onRefresh
            )
        }

        else -> {
            WalkerWalksList(
                walks = displayedWalks,
                state = state,
                onStart = onStart,
                onComplete = onComplete,
                showActions = showActions,
                modifier = modifier
            )
        }
    }
}
