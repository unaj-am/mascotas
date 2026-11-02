
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
import com.app.features.walks.walker.component.WalkerAgendaNavigation
import com.app.features.walks.walker.component.WalkerWalksContent
import com.app.features.walks.walker.component.WalkerWalksFeedback
import com.app.features.walks.walker.component.WalkerWalksHeader

@Composable
fun WalkerAgendaScreen(
    state: WalkerWalksUiState,
    onRefresh: () -> Unit,
    onActiveWalkClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onStart: (String) -> Unit,
    onComplete: (String) -> Unit
) {
    val agenda = state.walks.filter { walk ->
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
                title = "Agenda",
                subtitle = "Organizá los paseos programados y consultá los que están en curso.",
                isLoading = state.isLoading,
                onRefresh = onRefresh
            )
        }

        AppAnimatedVisibility(visible = true) {
            WalkerAgendaNavigation(
                onActiveWalkClick = onActiveWalkClick,
                onHistoryClick = onHistoryClick
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
                displayedWalks = agenda,
                loadingMessage = "Cargando agenda...",
                emptyTitle = "Agenda vacía",
                emptyMessage = "No tenés paseos programados ni paseos en curso.",
                onRefresh = onRefresh,
                onStart = onStart,
                onComplete = onComplete,
                showActions = true,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
