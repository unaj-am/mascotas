package com.app.features.walks.owner

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
import com.app.features.walks.component.WalkRequestCard

@Composable
fun OwnerWalksScreen(
    ownerId: String,
    onTrackWalk: (String) -> Unit,
    viewModel: OwnerWalksViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(ownerId) { viewModel.load(ownerId, forceRefresh = true) }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        androidx.compose.foundation.layout.Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Mis paseos", style = MaterialTheme.typography.headlineMedium)
            TextButton(onClick = viewModel::refresh, enabled = !state.isLoading && state.processingId == null) { Text("Actualizar") }
        }
        state.operationMessage?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
        state.operationError?.let { DomainErrorMessage(message = it, onRetry = viewModel::refresh) }
        state.errorMessage?.let { DomainErrorMessage(message = it, onRetry = viewModel::refresh) }
        when {
            state.isLoading && state.walks.isEmpty() && state.requests.isEmpty() -> LoadingContent(Modifier.weight(1f))
            state.walks.isEmpty() && state.requests.isEmpty() && state.errorMessage == null -> EmptyContent("Todavía no tenés paseos ni solicitudes. Buscá un paseador para organizar el primero.", Modifier.weight(1f), "Actualizar", viewModel::refresh)
            else -> LazyColumn(modifier = Modifier.weight(1f), contentPadding = PaddingValues(bottom = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (state.requests.isNotEmpty()) {
                    item { Text("SOLICITUDES", style = MaterialTheme.typography.titleSmall) }
                    items(state.requests, key = { "request-${it.id}" }) { request ->
                        WalkRequestCard(request, state.processingId == request.id) { viewModel.cancelRequest(request.id) }
                    }
                }
                val upcoming = state.walks.filter { it.status == WalkStatus.SCHEDULED || it.status == WalkStatus.IN_PROGRESS }
                val previous = state.walks.filter { it.status == WalkStatus.COMPLETED || it.status == WalkStatus.CANCELLED }
                if (upcoming.isNotEmpty()) {
                    item { Text("PRÓXIMOS Y EN CURSO", style = MaterialTheme.typography.titleSmall) }
                    items(upcoming, key = { "walk-${it.id}" }) { walk ->
                        WalkCard(walk = walk, isProcessing = state.processingId == walk.id, showTrack = walk.status == WalkStatus.IN_PROGRESS, showCancel = walk.status == WalkStatus.SCHEDULED, onTrack = { onTrackWalk(walk.id) }, onCancel = { viewModel.cancelScheduledWalk(walk.id) })
                    }
                }
                if (previous.isNotEmpty()) {
                    item { Text("HISTORIAL", style = MaterialTheme.typography.titleSmall) }
                    items(previous, key = { "past-${it.id}" }) { walk -> WalkCard(walk = walk) }
                }
            }
        }
    }
}
