
package com.app.features.walks.owner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.ui.components.AppAnimatedVisibility
import com.app.core.ui.theme.DimensUI
import com.app.features.walks.owner.component.OwnerWalksContent
import com.app.features.walks.owner.component.OwnerWalksFeedback
import com.app.features.walks.owner.component.OwnerWalksFilterBar
import com.app.features.walks.owner.component.OwnerWalksHeader

@Composable
fun OwnerWalksScreen(
    state: OwnerWalksUiState,
    onRefresh: () -> Unit,
    onTrackWalk: (String) -> Unit,
    onOpenWalk: (String) -> Unit,
    onCancelRequest: (String) -> Unit,
    onCancelWalk: (String) -> Unit,
    onFilterSelected: (OwnerWalkFilter) -> Unit
) {
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
            OwnerWalksHeader(
                onRefresh = onRefresh
            )
        }

        OwnerWalksFeedback(
            operationMessage = state.operationMessage,
            operationError = state.operationError,
            loadingError = state.errorMessage,
            onRetry = onRefresh
        )

        AppAnimatedVisibility(
            visible = state.walks.isNotEmpty() ||
                    state.requests.isNotEmpty()
        ) {
            OwnerWalksFilterBar(
                walks = state.walks,
                requests = state.requests,
                selectedFilter = state.selectedFilter,
                onFilterSelected = onFilterSelected
            )
        }

        AppAnimatedVisibility(
            visible = true,
            modifier = Modifier.weight(1f)
        ) {
            OwnerWalksContent(
                state = state,
                onRefresh = onRefresh,
                onTrackWalk = onTrackWalk,
                onOpenWalk = onOpenWalk,
                onCancelRequest = onCancelRequest,
                onCancelWalk = onCancelWalk,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
