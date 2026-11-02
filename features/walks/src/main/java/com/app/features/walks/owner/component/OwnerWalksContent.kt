
package com.app.features.walks.owner.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.ui.components.EmptyContent
import com.app.core.ui.components.ErrorContent
import com.app.core.ui.components.LoadingContent
import com.app.features.walks.owner.OwnerWalksUiState

@Composable
fun OwnerWalksContent(
    state: OwnerWalksUiState,
    onRefresh: () -> Unit,
    onTrackWalk: (String) -> Unit,
    onOpenWalk: (String) -> Unit,
    onCancelRequest: (String) -> Unit,
    onCancelWalk: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val hasData = state.walks.isNotEmpty() ||
            state.requests.isNotEmpty()

    when {
        state.isLoading && !hasData -> {
            LoadingContent(
                modifier = modifier,
                message = "Cargando tus paseos..."
            )
        }

        state.errorMessage != null && !hasData -> {
            ErrorContent(
                message = state.errorMessage,
                onRetry = onRefresh,
                modifier = modifier
            )
        }

        !state.isLoading &&
                state.errorMessage == null &&
                !hasData -> {
            EmptyContent(
                title = "Todavía no tenés paseos",
                message = "Buscá un paseador y enviá una solicitud para organizar tu primer paseo.",
                modifier = modifier,
                actionText = "Actualizar",
                onAction = onRefresh
            )
        }

        else -> {
            OwnerWalksList(
                state = state,
                onTrackWalk = onTrackWalk,
                onOpenWalk = onOpenWalk,
                onCancelRequest = onCancelRequest,
                onCancelWalk = onCancelWalk,
                modifier = modifier
            )
        }
    }
}
