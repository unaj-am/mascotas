
package com.app.features.walks.walker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.app.core.ui.theme.MascotasThemeUI
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    onRetry: () -> Unit,
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
                title = "Agenda",
                subtitle = "Gestioná tus paseos programados"
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
            onRetry = onRetry
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
                onRetry = onRetry,
                onStart = onStart,
                onComplete = onComplete,
                showActions = true,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WalkerAgendaScreenPreview() {
    MascotasThemeUI {
        WalkerAgendaScreen(
            state = WalkerWalksUiState(isLoading = false),
            onRetry = {},
            onActiveWalkClick = {},
            onHistoryClick = {},
            onStart = {},
            onComplete = {}
        )
    }
}
