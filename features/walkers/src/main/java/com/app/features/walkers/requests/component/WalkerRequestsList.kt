
package com.app.features.walkers.requests.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.ui.components.AppAnimatedVisibility
import com.app.core.ui.theme.DimensUI
import com.app.features.walkers.requests.WalkerRequestsUiState

@Composable
fun WalkerRequestsList(
    state: WalkerRequestsUiState,
    onAcceptRequest: (String) -> Unit,
    onRejectRequest: (String) -> Unit,
    onRequestClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
){
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            bottom = DimensUI.SpaceLarge
        ),
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SpaceMedium
        )
    ) {
        items(
            items = state.requests,
            key = { request -> request.id }
        ) { request ->
            AppAnimatedVisibility(
                visible = true,
                durationMillis = 240
            ) {
                WalkerRequestCard(
                    request = request,
                    ownerName = state.ownerNames[request.id]
                        ?: "Dueño no disponible",
                    petNames = state.petNames[request.id]
                        ?: emptyList(),
                    isProcessing =
                        state.processingRequestId == request.id,
                    isAnyRequestProcessing =
                        state.processingRequestId != null,
                    onAccept = {
                        onAcceptRequest(request.id)
                    },
                    onReject = {
                        onRejectRequest(request.id)
                    },
                    onRequestClick = {
                        onRequestClick(request.id)
                    }

                )
            }
        }
    }
}
