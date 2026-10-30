package com.app.features.walks.walker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.core.domain.model.WalkStatus
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
                horizontal = 20.dp,
                vertical = 16.dp
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        WalkerWalksHeader(
            title = "Agenda",
            subtitle = "Organizá los paseos programados y consultá los que están en curso.",
            onRefresh = onRefresh
        )

        WalkerAgendaNavigation(
            onActiveWalkClick = onActiveWalkClick,
            onHistoryClick = onHistoryClick
        )

        WalkerWalksFeedback(
            operationMessage = state.operationMessage,
            operationError = state.operationError,
            loadingError = state.errorMessage,
            onRetry = onRefresh
        )

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
            modifier = Modifier.weight(1f)
        )
    }
}