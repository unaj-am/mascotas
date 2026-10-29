
package com.app.core.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Etiqueta visual reutilizable para estados.
 *
 * El componente no conoce los estados del dominio.
 * La pantalla que lo utiliza define el texto y,
 * opcionalmente, sus colores.
 */
@Composable
fun StatusBadge(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color? = null,
    contentColor: Color? = null
) {
    val resolvedContainerColor =
        containerColor
            ?: MaterialTheme.colorScheme.secondaryContainer

    val resolvedContentColor =
        contentColor
            ?: MaterialTheme.colorScheme.onSecondaryContainer

    Surface(
        modifier = modifier,
        color = resolvedContainerColor,
        contentColor = resolvedContentColor,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            ),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium
        )
    }
}
