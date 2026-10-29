
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
fun WalkerActiveWalkScreen(
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

    val actionableWalks = state.walks.filter { walk ->
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
            title = "Paseos activos",
            subtitle = "Iniciá los paseos confirmados y finalizá los que estén en curso.",
            actionText = "Actualizar",
            onAction = viewModel::refresh
        )

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
                    message = "Cargando tus paseos..."
                )
            }

            actionableWalks.isEmpty() &&
                    state.errorMessage == null -> {
                EmptyContent(
                    message = "No tenés paseos confirmados pendientes de iniciar ni paseos en curso.",
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
                        items = actionableWalks,
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
