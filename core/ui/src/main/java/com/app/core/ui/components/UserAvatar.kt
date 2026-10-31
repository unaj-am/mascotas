
package com.app.core.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import com.app.core.ui.theme.DimensUI

@Composable
fun UserAvatar(
    name: String,
    modifier: Modifier = Modifier,
    image: Painter? = null,
    size: Dp = DimensUI.AvatarSizeMedium,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer
) {
    AppAvatar(
        name = name,
        modifier = modifier,
        image = image,
        size = size,
        backgroundColor = backgroundColor,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        textStyle = MaterialTheme.typography.titleMedium,
        description = "Fotografía de $name"
    )
}
