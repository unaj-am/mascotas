
package com.app.features.walkers.list.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.app.core.ui.components.ErrorContent
import com.app.core.ui.components.LoadingContent
import com.app.core.ui.components.PrimaryActionButton
import com.app.core.ui.theme.DimensUI
import com.app.features.walkers.list.WalkerUiState

@Composable
fun WalkerContent(
    uiState: WalkerUiState,
    onWalkerClick: (String) -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    when (uiState) {
        WalkerUiState.Loading -> {
            LoadingContent()
        }

        is WalkerUiState.Success -> {
            if (uiState.walkers.isEmpty()) {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(DimensUI.SpaceXLarge),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        DimensUI.SpaceLarge,
                        Alignment.CenterVertically
                    )
                ) {
                    Text(
                        text = "Cuando haya paseadores registrados, " +
                                "vas a poder consultar sus perfiles desde acá.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    PrimaryActionButton(
                        text = "Volver a intentar",
                        onClick = onRetry
                    )

                }
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
