
package com.app.features.walkers.requests

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.core.ui.components.DomainErrorMessage
import com.app.core.ui.components.EmptyContent
import com.app.core.ui.components.ErrorContent
import com.app.core.ui.components.LoadingContent
import com.app.core.ui.theme.DimensUI
import com.app.features.walkers.requests.component.WalkerRequestCard

@Composable
fun WalkerRequestsScreen(
    walkerId: String,
    viewModel: WalkerRequestsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(walkerId) {
        viewModel.loadRequests(walkerId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = DimensUI.ScreenHorizontalPadding,
                vertical = DimensUI.ScreenVerticalPadding
            ),
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SectionSpacing
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Solicitudes de paseo",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )

            TextButton(
                onClick = viewModel::refreshRequests,
                enabled = !state.isLoading &&
                        state.processingRequestId == null
            ) {
                Text("Actualizar")
            }
        }

        state.operationMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        state.operationError?.let { message ->
            DomainErrorMessage(
                message = message,
                onRetry = viewModel::refreshRequests
            )
        }

        when {
            state.isLoading && state.requests.isEmpty() -> {
                LoadingContent(
                    modifier = Modifier.weight(1f)
                )
            }

            state.loadingError != null &&
                    state.requests.isEmpty() -> {
                ErrorContent(
                    message = state.loadingError.orEmpty(),
                    modifier = Modifier.weight(1f),
                    onRetry = viewModel::refreshRequests
                )
            }

            state.requests.isEmpty() -> {
                EmptyContent(
                    message = "Todavía no tenés solicitudes de paseo.",
                    modifier = Modifier.weight(1f),
                    actionText = "Volver a cargar",
                    onAction = viewModel::refreshRequests
                )
            }

            else -> {
                state.loadingError?.let { message ->
                    DomainErrorMessage(
                        message = message,
                        onRetry = viewModel::refreshRequests
                    )
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(
                        bottom = DimensUI.SpaceLarge
                    ),
                    verticalArrangement = Arrangement.spacedBy(
                        DimensUI.SpaceMedium
                    )
                ) {
                    items(
                        items = state.requests,
                        key = { request -> request.id }
                    ) { request ->
                        WalkerRequestCard(
                            request = request,
                            ownerName = state.ownerNames[request.id]
                                ?: "Dueño no disponible",
                            petNames = state.petNames[request.id]
                                ?: emptyList(),
                            isProcessing =
                                state.processingRequestId == request.id,
                            isAnyRequestProcessing =
                                state.processingRequestId != null,
                            onAccept = {
                                viewModel.acceptRequest(request.id)
                            },
                            onReject = {
                                viewModel.rejectRequest(request.id)
                            }
                        )
                    }
                }
            }
        }
    }
}
