
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
 * Puede utilizarse en una pantalla completa o dentro
 * de un contenedor más pequeño.
 *
 * No inicia operaciones ni administra el estado
 * de carga de la aplicación.
 */
@Composable
fun LoadingContent(
    modifier: Modifier = Modifier.fillMaxSize(),
    message: String? = null
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                DimensUI.SpaceLarge
            )
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )

            if (!message.isNullOrBlank()) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Estado visual para listas o resultados vacíos.
 *
 * El contenido y la acción dependen de la pantalla
 * que utilice este componente.
 */
@Composable
fun EmptyContent(
    message: String,
    modifier: Modifier = Modifier.fillMaxSize(),
    title: String? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    illustration: (@Composable () -> Unit)? = null
) {
    Box(
        modifier = modifier,
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
            if (illustration != null) {
                illustration()
            }

            if (!title.isNullOrBlank()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }

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
 * Estado visual de error.
 *
 * Reutiliza DomainErrorMessage para conservar
 * la presentación compartida de los errores.
 */
@Composable
fun ErrorContent(
    message: String,
    modifier: Modifier = Modifier.fillMaxSize(),
    onRetry: (() -> Unit)? = null
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        DomainErrorMessage(
            message = message,
            modifier = Modifier
                .fillMaxWidth()
                .padding(DimensUI.SpaceLarge),
            onRetry = onRetry
        )
    }
}
