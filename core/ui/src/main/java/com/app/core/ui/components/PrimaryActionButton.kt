
package com.app.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.app.core.ui.theme.DimensUI

/**
 * Botón principal reutilizable de Mascotas.
 *
 * Permite configurar su ancho, estado de carga
 * y habilitación sin administrar lógica de negocio.
 */
@Composable
fun PrimaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    fullWidth: Boolean = true
) {
    val buttonModifier = if (fullWidth) {
        modifier
            .fillMaxWidth()
            .heightIn(min = DimensUI.ButtonMinHeight)
    } else {
        modifier.heightIn(
            min = DimensUI.ButtonMinHeight
        )
    }

    Button(
        onClick = onClick,
        modifier = buttonModifier,
        enabled = enabled && !isLoading,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                DimensUI.SpaceSmall
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(
                        DimensUI.IconSizeSmall
                    ),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = DimensUI.SpaceXXSmall
                )
            }

            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
