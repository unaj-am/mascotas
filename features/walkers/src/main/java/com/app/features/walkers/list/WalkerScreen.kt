package com.app.features.walkers.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.core.domain.model.Walker
import com.app.core.ui.theme.DimensUI
import com.app.features.walkers.list.component.WalkerContent
import com.app.features.walkers.list.component.WalkerHeader

@Composable
fun WalkerRoute(
    viewModel: WalkerViewModel,
    onWalkerClick: (String) -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WalkerScreen(
        uiState = uiState,
        onWalkerClick = onWalkerClick,
        onRetry = viewModel::loadWalkers,
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
fun WalkerScreen(
    uiState: WalkerUiState,
    onWalkerClick: (String) -> Unit = {},
    onRetry: () -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = DimensUI.ScreenHorizontalPadding,
                vertical = DimensUI.ScreenVerticalPadding
            ),
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SectionSpacing
        )
    ) {
        // Botón para volver a la pantalla anterior.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onBack
            ) {
                Text(
                    text = "← Volver",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        // Encabezado de la sección de paseadores.
        WalkerHeader(uiState = uiState)

        // Contenido principal: carga, lista vacía, resultados o error.
        Box(
            modifier = Modifier.weight(1f)
        ) {
            WalkerContent(
                uiState = uiState,
                onWalkerClick = onWalkerClick,
                onRetry = onRetry,
                onBack = onBack
            )
        }
    }
}