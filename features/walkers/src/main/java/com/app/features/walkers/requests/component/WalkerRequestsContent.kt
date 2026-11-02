
package com.app.features.walkers.requests.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.ui.components.DomainErrorMessage
import com.app.core.ui.components.EmptyContent
import com.app.core.ui.components.ErrorContent
import com.app.core.ui.components.LoadingContent
import com.app.features.walkers.requests.WalkerRequestsUiState

@Composable
fun WalkerRequestsContent(
    state: WalkerRequestsUiState,
    onRefresh: () -> Unit,
    onAcceptRequest: (String) -> Unit,
    onRejectRequest: (String) -> Unit,
    onRequestClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    when {
        state.isLoading && state.requests.isEmpty() -> {
            LoadingContent(
                modifier = modifier,
                message = "Cargando solicitudes..."
            )
        }

        state.loadingError != null && state.requests.isEmpty() -> {
            ErrorContent(
                message = state.loadingError,
                modifier = modifier,
                onRetry = onRefresh
            )
        }

        state.requests.isEmpty() -> {
            EmptyContent(
                title = "No tenés solicitudes todavía",
                message = "Cuando un dueño solicite un paseo, vas a poder revisarlo desde esta pantalla.",
                modifier = modifier,
                actionText = "Actualizar",
                onAction = onRefresh
            )
        }

        state.filteredRequests.isEmpty() -> {
            EmptyContent(
                title = "No hay solicitudes ${state.selectedFilter.label.lowercase()}",
                message = "Probá seleccionando otro filtro para consultar las demás solicitudes.",
                modifier = modifier
            )
        }

        else -> {
            Column(
                modifier = modifier.fillMaxWidth()
            ) {
                state.loadingError?.let { message ->
                    DomainErrorMessage(
                        message = message,
                        onRetry = onRefresh
                    )
                }

                WalkerRequestsList(
                    state = state,
                    onAcceptRequest = onAcceptRequest,
                    onRejectRequest = onRejectRequest,
                    onRequestClick = onRequestClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
