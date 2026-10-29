package com.app.features.walks.owner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun OwnerWalksRoute(
    ownerId: String,
    onTrackWalk: (String) -> Unit,
    onOpenWalk: (String) -> Unit = {},
    viewModel: OwnerWalksViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(ownerId) {
        viewModel.load(
            ownerId = ownerId,
            forceRefresh = true
        )
    }

    OwnerWalksScreen(
        state = state,
        onRefresh = viewModel::refresh,
        onTrackWalk = onTrackWalk,
        onOpenWalk = onOpenWalk,
        onCancelRequest = viewModel::cancelRequest,
        onCancelWalk = viewModel::cancelScheduledWalk
    )
}