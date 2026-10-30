package com.app.features.walks.walker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WalkerActiveWalkRoute(
    walkerId: String,
    viewModel: WalkerWalksViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(walkerId) {
        viewModel.load(
            walkerId = walkerId,
            forceRefresh = true
        )
    }

    WalkerActiveWalkScreen(
        state = state,
        onRefresh = viewModel::refresh,
        onStart = viewModel::start,
        onComplete = viewModel::complete
    )
}