
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
import androidx.compose.ui.unit.Dp
import com.app.core.ui.theme.DimensUI
import com.app.core.ui.theme.ShapeUI

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    containerColor: Color? = null,
    contentColor: Color? = null,
    border: BorderStroke? = null,
    contentPadding: Dp = DimensUI.CardPadding,
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

    val cardBorder = border ?: BorderStroke(
        width = DimensUI.DividerThickness,
        color = MaterialTheme.colorScheme.outlineVariant
    )

    val cardElevation = CardDefaults.cardElevation(
        defaultElevation = DimensUI.SpaceXSmall,
        pressedElevation = DimensUI.SpaceSmall,
        focusedElevation = DimensUI.SpaceXSmall,
        hoveredElevation = DimensUI.SpaceSmall,
        disabledElevation = DimensUI.SpaceNone
    )

    val cardContent: @Composable ColumnScope.() -> Unit = {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }

    if (onClick == null) {
        Card(
            modifier = modifier,
            shape = ShapeUI.Card,
            colors = cardColors,
            elevation = cardElevation,
            border = cardBorder,
            content = cardContent
        )
    } else {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = ShapeUI.Card,
            colors = cardColors,
            elevation = cardElevation,
            border = cardBorder,
            content = cardContent
        )
    }
}
