package com.app.features.walks.walker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.features.walks.walker.component.rememberWalkerWalkActionCallbacks

@Composable
fun WalkerActiveWalkRoute(
    walkerId: String,
    onBack: () -> Unit,
    viewModel: WalkerWalksViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val actions = rememberWalkerWalkActionCallbacks(viewModel)

    LaunchedEffect(walkerId) {
        viewModel.load(
            walkerId = walkerId,
            forceRefresh = true
        )
    }

    WalkerActiveWalkScreen(
        state = state,
        onRetry = viewModel::retryLoad,
        onStart = actions.onStart,
        onComplete = actions.onComplete,
        onBack = onBack
    )
}
