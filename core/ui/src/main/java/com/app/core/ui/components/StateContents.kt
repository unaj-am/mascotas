package com.app.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.app.core.ui.theme.DimensUI

/**
 * Estado visual de carga.
 *
 * No inicia operaciones ni administra
 * el estado de la pantalla.
 */
@Composable
fun LoadingContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary
        )
    }
}

/**
 * Estado visual para listas o resultados vacíos.
 *
 * El mensaje y la acción dependen de la pantalla
 * que utilice este componente.
 */
@Composable
fun EmptyContent(
    message: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DimensUI.SpaceXLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                DimensUI.SpaceLarge
            )
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            if (!actionText.isNullOrBlank() && onAction != null) {
                PrimaryActionButton(
                    text = actionText,
                    onClick = onAction
                )
            }
        }
    }
}

/**
 * Estado visual de error de pantalla completa.
 *
 * Reutiliza DomainErrorMessage para mantener
 * consistente la presentación de errores.
 */
@Composable
fun ErrorContent(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        DomainErrorMessage(
            message = message,
            modifier = Modifier.padding(
                DimensUI.SpaceLarge
            ),
            onRetry = onRetry
        )
    }
}