package com.app.features.walks.owner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun OwnerWalkDetailRoute(
    walkId: String,
    onBack: () -> Unit,
    onTrackWalk: (String) -> Unit,
    viewModel: OwnerWalkDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(walkId) {
        viewModel.load(
            walkId = walkId,
            forceRefresh = true
        )
    }

    OwnerWalkDetailScreen(
        state = state,
        onBack = onBack,
        onRefresh = viewModel::refresh,
        onTrackWalk = onTrackWalk
    )
}