
package com.app.features.walks.walker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.features.walks.walker.component.rememberWalkerWalkActionCallbacks

@Composable
fun WalkerAgendaRoute(
    walkerId: String,
    onActiveWalkClick: () -> Unit,
    onHistoryClick: () -> Unit,
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

    WalkerAgendaScreen(
        state = state,
        onRefresh = viewModel::refresh,
        onActiveWalkClick = onActiveWalkClick,
        onHistoryClick = onHistoryClick,
        onStart = actions.onStart,
        onComplete = actions.onComplete
    )
}
