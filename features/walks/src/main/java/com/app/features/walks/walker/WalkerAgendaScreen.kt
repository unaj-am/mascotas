
package com.app.features.walks.walker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import com.app.core.ui.components.SectionHeader
import com.app.features.walks.component.WalkCard

@Composable
fun WalkerAgendaScreen(
    walkerId: String,
    onActiveWalkClick: () -> Unit,
    onHistoryClick: () -> Unit,
    viewModel: WalkerWalksViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(walkerId) {
        viewModel.load(
            walkerId = walkerId,
            forceRefresh = true
        )
    }

    val agenda = state.walks.filter { walk ->
        walk.status == WalkStatus.SCHEDULED ||
                walk.status == WalkStatus.IN_PROGRESS
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionHeader(
            title = "Agenda",
            subtitle = "Organizá los paseos programados y consultá los que están en curso.",
            actionText = "Actualizar",
            onAction = viewModel::refresh
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onActiveWalkClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("Paseo activo")
            }

            OutlinedButton(
                onClick = onHistoryClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("Historial")
            }
        }

        state.operationMessage?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
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
            state.isLoading && state.walks.isEmpty() -> {
                LoadingContent(
                    modifier = Modifier.weight(1f),
                    message = "Cargando agenda..."
                )
            }

            agenda.isEmpty() && state.errorMessage == null -> {
                EmptyContent(
                    title = "Agenda vacía",
                    message = "No tenés paseos programados ni paseos en curso.",
                    modifier = Modifier.weight(1f),
                    actionText = "Actualizar",
                    onAction = viewModel::refresh
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(
                        bottom = 20.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = agenda,
                        key = { walk -> walk.id }
                    ) { walk ->
                        val details = state.walkDetails[walk.id]

                        WalkCard(
                            walk = walk,
                            ownerName = details?.ownerName,
                            petNames = details?.petNames.orEmpty(),
                            isProcessing = state.processingId != null,
                            showStart = walk.status == WalkStatus.SCHEDULED,
                            showComplete = walk.status == WalkStatus.IN_PROGRESS,
                            onStart = {
                                viewModel.start(walk.id)
                            },
                            onComplete = {
                                viewModel.complete(walk.id)
                            }
                        )
                    }
                }
            }
        }
    }
}
