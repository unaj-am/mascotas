package com.app.features.walks.walker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.core.domain.model.WalkStatus
import com.app.core.ui.components.DomainErrorMessage
import com.app.core.ui.components.EmptyContent
import com.app.core.ui.components.LoadingContent
import com.app.features.walks.component.WalkCard

@Composable
fun WalkerHistoryScreen(
    walkerId: String,
    viewModel: WalkerWalksViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(walkerId) { viewModel.load(walkerId, forceRefresh = true) }
    val history = state.walks.filter { it.status == WalkStatus.COMPLETED || it.status == WalkStatus.CANCELLED }.sortedByDescending { it.scheduledAt }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        androidx.compose.foundation.layout.Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Historial", style = MaterialTheme.typography.headlineMedium)
            TextButton(onClick = viewModel::refresh, enabled = !state.isLoading) { Text("Actualizar") }
        }
        state.errorMessage?.let { DomainErrorMessage(message = it, onRetry = viewModel::refresh) }
        when {
            state.isLoading && state.walks.isEmpty() -> LoadingContent(Modifier.weight(1f))
            history.isEmpty() && state.errorMessage == null -> EmptyContent("Todavía no hay paseos finalizados o cancelados.", Modifier.weight(1f), "Actualizar", viewModel::refresh)
            else -> LazyColumn(modifier = Modifier.weight(1f), contentPadding = PaddingValues(bottom = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(history, key = { it.id }) { walk -> WalkCard(walk = walk) }
            }
        }
    }
}
