
package com.app.features.walks.owner.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.domain.model.WalkStatus
import com.app.core.ui.components.SectionHeader
import com.app.core.ui.theme.DimensUI
import com.app.features.walks.component.WalkCard
import com.app.features.walks.component.WalkRequestCard
import com.app.features.walks.owner.OwnerWalksUiState

@Composable
fun OwnerWalksList(
    state: OwnerWalksUiState,
    onTrackWalk: (String) -> Unit,
    onOpenWalk: (String) -> Unit,
    onCancelRequest: (String) -> Unit,
    onCancelWalk: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val upcoming = state.walks.filter { walk ->
        walk.status == WalkStatus.SCHEDULED ||
                walk.status == WalkStatus.IN_PROGRESS
    }

    val previous = state.walks.filter { walk ->
        walk.status == WalkStatus.COMPLETED ||
                walk.status == WalkStatus.CANCELLED
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            bottom = DimensUI.SpaceXLarge
        ),
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.CardSpacing
        )
    ) {
        if (state.requests.isNotEmpty()) {
            item(key = "requests-header") {
                SectionHeader(
                    title = "Solicitudes",
                    subtitle = "Estado de las solicitudes que enviaste."
                )
            }

            items(
                items = state.requests,
                key = { request -> "request-${request.id}" }
            ) { request ->
                WalkRequestCard(
                    request = request,
                    isProcessing = state.processingId == request.id,
                    onCancel = {
                        onCancelRequest(request.id)
                    }
                )
            }
        }

        if (upcoming.isNotEmpty()) {
            item(key = "upcoming-header") {
                SectionHeader(
                    title = "Próximos y en curso",
                    subtitle = "Tus paseos confirmados y los que están activos."
                )
            }

            items(
                items = upcoming,
                key = { walk -> "walk-${walk.id}" }
            ) { walk ->
                WalkCard(
                    walk = walk,
                    isProcessing = state.processingId == walk.id,
                    showTrack = walk.status == WalkStatus.IN_PROGRESS,
                    showCancel = walk.status == WalkStatus.SCHEDULED,
                    onTrack = {
                        onTrackWalk(walk.id)
                    },
                    onCancel = {
                        onCancelWalk(walk.id)
                    },
                    onClick = {
                        onOpenWalk(walk.id)
                    }
                )
            }
        }

        if (previous.isNotEmpty()) {
            item(key = "history-header") {
                SectionHeader(
                    title = "Historial",
                    subtitle = "Paseos finalizados o cancelados."
                )
            }

            items(
                items = previous,
                key = { walk -> "past-${walk.id}" }
            ) { walk ->
                WalkCard(
                    walk = walk,
                    onClick = {
                        onOpenWalk(walk.id)
                    }
                )
            }
        }
    }
}
