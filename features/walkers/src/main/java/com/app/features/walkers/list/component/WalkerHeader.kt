package com.app.features.walkers.list.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.ui.theme.DimensUI
import com.app.features.walkers.list.WalkerUiState

@Composable
fun WalkerHeader(
    uiState: WalkerUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SpaceXSmall
        )
    ) {
        Text(
            text = "PASEADORES",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = "Encontrá tu paseador",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = when (uiState) {
                WalkerUiState.Loading ->
                    "Buscando paseadores."

                is WalkerUiState.Success ->
                    if (uiState.walkers.isEmpty()) {
                        "Todavía no hay paseadores registrados."
                    } else {
                        "Encontramos ${uiState.walkers.size} paseadores."
                    }

                is WalkerUiState.Error ->
                    "No pudimos cargar los paseadores."
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}