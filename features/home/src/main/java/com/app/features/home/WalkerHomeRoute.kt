package com.app.features.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WalkerHomeRoute(
    userId: String,
    viewModel: WalkerHomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        viewModel.loadHome(userId)
    }

    WalkerHomeScreen(
        state = state,
        onAvailabilityChange = viewModel::onAvailabilityChange,
        onRetry = viewModel::retryLoadHome
    )
}