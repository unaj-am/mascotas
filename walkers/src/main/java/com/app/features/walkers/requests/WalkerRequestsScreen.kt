
package com.app.features.walkers.requests

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.ui.components.AppAnimatedVisibility
import com.app.core.ui.theme.DimensUI
import com.app.features.walkers.requests.component.WalkerRequestsContent
import com.app.features.walkers.requests.component.WalkerRequestsFeedback
import com.app.features.walkers.requests.component.WalkerRequestsFilterBar
import com.app.features.walkers.requests.component.WalkerRequestsHeader

@Composable
fun WalkerRequestsScreen(
    state: WalkerRequestsUiState,
    onRefresh: () -> Unit,
    onAcceptRequest: (String) -> Unit,
    onRejectRequest: (String) -> Unit,
    onRequestClick: (String) -> Unit = {},
    onFilterSelected: (WalkerRequestFilter) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = DimensUI.ScreenHorizontalPadding,
                vertical = DimensUI.ScreenVerticalPadding
            ),
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SectionSpacing
        )
    ) {
        AppAnimatedVisibility(
            visible = true,
            durationMillis = 260
        ) {
            WalkerRequestsHeader(
                requestCount = state.requests.size,
                isLoading = state.isLoading,
                isProcessing = state.processingRequestId != null,
                onRefresh = onRefresh
            )
        }

        AppAnimatedVisibility(
            visible = state.operationMessage != null ||
                    state.operationError != null,
            durationMillis = 220
        ) {
            WalkerRequestsFeedback(
                operationMessage = state.operationMessage,
                operationError = state.operationError,
                onRetry = onRefresh
            )
        }

        AppAnimatedVisibility(
            visible = state.requests.isNotEmpty(),
            durationMillis = 220
        ) {
            WalkerRequestsFilterBar(
                requests = state.requests,
                selectedFilter = state.selectedFilter,
                onFilterSelected = onFilterSelected
            )
        }

        AppAnimatedVisibility(
            visible = true,
            modifier = Modifier.weight(1f),
            durationMillis = 320
        ) {
            WalkerRequestsContent(
                state = state,
                onRefresh = onRefresh,
                onAcceptRequest = onAcceptRequest,
                onRejectRequest = onRejectRequest,
                onRequestClick = onRequestClick,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
