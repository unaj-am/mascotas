
package com.app.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.core.ui.theme.DimensUI

/**
 * Tarjeta visual reutilizable de Mascotas.
 *
 * Permite mostrar contenido personalizado dentro
 * de un contenedor con estilo consistente.
 *
 * Si onClick es null, la tarjeta no es pulsable.
 * No contiene lógica de negocio.
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    containerColor: Color? = null,
    contentColor: Color? = null,
    border: BorderStroke? = null,
    contentPadding: androidx.compose.ui.unit.Dp =
        DimensUI.SpaceLarge,
    content: @Composable ColumnScope.() -> Unit
) {
    val resolvedContainerColor =
        containerColor ?: MaterialTheme.colorScheme.surface

    val resolvedContentColor =
        contentColor ?: MaterialTheme.colorScheme.onSurface

    val cardColors = CardDefaults.cardColors(
        containerColor = resolvedContainerColor,
        contentColor = resolvedContentColor
    )

    val cardElevation = CardDefaults.cardElevation(
        defaultElevation = 2.dp
    )

    if (onClick == null) {
        Card(
            modifier = modifier,
            shape = MaterialTheme.shapes.large,
            colors = cardColors,
            elevation = cardElevation,
            border = border
        ) {
            Column(
                modifier = Modifier.padding(contentPadding),
                content = content
            )
        }
    } else {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = MaterialTheme.shapes.large,
            colors = cardColors,
            elevation = cardElevation,
            border = border
        ) {
            Column(
                modifier = Modifier.padding(contentPadding),
                content = content
            )
        }
    }
}
