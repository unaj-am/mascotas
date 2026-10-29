package com.app.features.walks.owner.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.ui.components.EmptyContent
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
    when {
        state.isLoading &&
                state.walks.isEmpty() &&
                state.requests.isEmpty() -> {

            LoadingContent(
                modifier = modifier,
                message = "Cargando tus paseos..."
            )
        }

        state.walks.isEmpty() &&
                state.requests.isEmpty() &&
                state.errorMessage == null -> {

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