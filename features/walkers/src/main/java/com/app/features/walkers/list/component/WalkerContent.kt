package com.app.features.walkers.list.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.ui.components.EmptyContent
import com.app.core.ui.components.ErrorContent
import com.app.core.ui.components.LoadingContent
import com.app.features.walkers.list.WalkerUiState

@Composable
fun WalkerContent(
    uiState: WalkerUiState,
    onWalkerClick: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        WalkerUiState.Loading -> {
            LoadingContent()
        }

        is WalkerUiState.Success -> {
            if (uiState.walkers.isEmpty()) {
                EmptyContent(
                    message = "Cuando haya paseadores registrados, vas a poder consultar sus perfiles desde acá.",
                    actionText = "Volver a intentar",
                    onAction = onRetry
                )
            } else {
                WalkerList(
                    walkers = uiState.walkers,
                    onWalkerClick = onWalkerClick,
                    modifier = modifier.fillMaxSize()
                )
            }
        }

        is WalkerUiState.Error -> {
            ErrorContent(
                message = uiState.message,
                onRetry = onRetry
            )
        }
    }
}