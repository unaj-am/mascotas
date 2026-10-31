
package com.app.features.walks.walker.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkStatus
import com.app.core.ui.theme.DimensUI
import com.app.features.walks.component.WalkCard
import com.app.features.walks.walker.WalkerWalksUiState

@Composable
fun WalkerWalksList(
    walks: List<Walk>,
    state: WalkerWalksUiState,
    onStart: (String) -> Unit,
    onComplete: (String) -> Unit,
    showActions: Boolean,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            bottom = DimensUI.SpaceXLarge
        ),
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.CardSpacing
        )
    ) {
        items(
            items = walks,
            key = { walk -> walk.id }
        ) { walk ->
            val details = state.walkDetails[walk.id]
            val isProcessing = showActions &&
                    state.processingId == walk.id

            WalkCard(
                walk = walk,
                ownerName = details?.ownerName,
                petNames = details?.petNames.orEmpty(),
                isProcessing = isProcessing,
                showStart = showActions &&
                        walk.status == WalkStatus.SCHEDULED,
                showComplete = showActions &&
                        walk.status == WalkStatus.IN_PROGRESS,
                onStart = {
                    onStart(walk.id)
                },
                onComplete = {
                    onComplete(walk.id)
                }
            )
        }
    }
}
