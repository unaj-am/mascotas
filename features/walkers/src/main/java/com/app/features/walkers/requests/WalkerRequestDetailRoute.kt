
package com.app.features.walkers.requests

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WalkerRequestDetailRoute(
    requestId: String,
    viewModel: WalkerRequestDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(requestId) {
        viewModel.loadRequest(requestId)
    }

    WalkerRequestDetailScreen(
        state = state
    )

}
