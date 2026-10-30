package com.app.features.walks.owner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.features.walks.owner.component.OwnerWalksContent
import com.app.features.walks.owner.component.OwnerWalksFeedback
import com.app.features.walks.owner.component.OwnerWalksHeader

@Composable
fun OwnerWalksScreen(
    state: OwnerWalksUiState,
    onRefresh: () -> Unit,
    onTrackWalk: (String) -> Unit,
    onOpenWalk: (String) -> Unit,
    onCancelRequest: (String) -> Unit,
    onCancelWalk: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OwnerWalksHeader(
            onRefresh = onRefresh
        )

        OwnerWalksFeedback(
            operationMessage = state.operationMessage,
            operationError = state.operationError,
            loadingError = state.errorMessage,
            onRetry = onRefresh
        )

        OwnerWalksContent(
            state = state,
            onRefresh = onRefresh,
            onTrackWalk = onTrackWalk,
            onOpenWalk = onOpenWalk,
            onCancelRequest = onCancelRequest,
            onCancelWalk = onCancelWalk,
            modifier = Modifier.weight(1f)
        )
    }
}