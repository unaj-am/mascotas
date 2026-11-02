
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
fun WalkerActiveWalkScreen(
    state: WalkerWalksUiState,
    onRefresh: () -> Unit,
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
            onRetry = onRefresh
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
                onRefresh = onRefresh,
                onStart = onStart,
                onComplete = onComplete,
                showActions = true,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
