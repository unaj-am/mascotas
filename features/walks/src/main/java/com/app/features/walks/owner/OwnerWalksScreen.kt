
package com.app.features.walks.owner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Mis paseos",
                style = MaterialTheme.typography.headlineMedium
            )

            TextButton(
                onClick = viewModel::refresh,
                enabled = !state.isLoading &&
                        state.processingId == null
            ) {
                Text("Actualizar")
            }
        }

        state.operationMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.primary
            )
        }

        state.operationError?.let { error ->
            DomainErrorMessage(
                message = error,
                onRetry = viewModel::refresh
            )
        }

        state.errorMessage?.let { error ->
            DomainErrorMessage(
                message = error,
                onRetry = viewModel::refresh
            )
        }

        when {
            state.isLoading &&
                    state.walks.isEmpty() &&
                    state.requests.isEmpty() -> {

                LoadingContent(
                    modifier = Modifier.weight(1f)
                )
            }

            state.walks.isEmpty() &&
                    state.requests.isEmpty() &&
                    state.errorMessage == null -> {

                EmptyContent(
                    message = "Todavía no tenés paseos ni solicitudes. Buscá un paseador para organizar el primero.",
                    modifier = Modifier.weight(1f)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(
                        bottom = 20.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Solicitudes que todavía no se convirtieron
                    // en paseos confirmados.
                    if (state.requests.isNotEmpty()) {
                        item {
                            Text(
                                text = "SOLICITUDES",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }

                        items(
                            items = state.requests,
                            key = { request -> "request-${request.id}" }
                        ) { request ->
                            WalkRequestCard(
                                request = request,
                                isProcessing =
                                    state.processingId == request.id,
                                onCancel = {
                                    viewModel.cancelRequest(request.id)
                                }
                            )
                        }
                    }

                    val upcoming = state.walks.filter { walk ->
                        walk.status == WalkStatus.SCHEDULED ||
                                walk.status == WalkStatus.IN_PROGRESS
                    }

                    val previous = state.walks.filter { walk ->
                        walk.status == WalkStatus.COMPLETED ||
                                walk.status == WalkStatus.CANCELLED
                    }

                    // Paseos programados y en curso.
                    if (upcoming.isNotEmpty()) {
                        item {
                            Text(
                                text = "PRÓXIMOS Y EN CURSO",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }

                        items(
                            items = upcoming,
                            key = { walk -> "walk-${walk.id}" }
                        ) { walk ->
                            WalkCard(
                                walk = walk,
                                isProcessing =
                                    state.processingId == walk.id,
                                showTrack =
                                    walk.status == WalkStatus.IN_PROGRESS,
                                showCancel =
                                    walk.status == WalkStatus.SCHEDULED,
                                onTrack = {
                                    onTrackWalk(walk.id)
                                },
                                onCancel = {
                                    viewModel.cancelScheduledWalk(walk.id)
                                },
                                onClick = {
                                    onOpenWalk(walk.id)
                                }
                            )
                        }
                    }

                    // Paseos finalizados o cancelados.
                    if (previous.isNotEmpty()) {
                        item {
                            Text(
                                text = "HISTORIAL",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }

                        items(
                            items = previous,
                            key = { walk -> "past-${walk.id}" }
                        ) { walk ->
                            WalkCard(
                                walk = walk,
                                onClick = {
                                    onOpenWalk(walk.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
