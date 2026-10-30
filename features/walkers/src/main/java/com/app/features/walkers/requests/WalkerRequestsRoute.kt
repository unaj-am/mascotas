package com.app.features.walkers.requests

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WalkerRequestsRoute(
    walkerId: String,
    viewModel: WalkerRequestsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(walkerId) {
        viewModel.loadRequests(walkerId)
    }

    WalkerRequestsScreen(
        state = state,
        onRefresh = viewModel::refreshRequests,
        onAcceptRequest = viewModel::acceptRequest,
        onRejectRequest = viewModel::rejectRequest
    )
}