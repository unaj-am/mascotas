
package com.app.core.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import com.app.core.ui.theme.DimensUI

@Composable
fun PetAvatar(
    name: String,
    modifier: Modifier = Modifier,
    image: Painter? = null,
    size: Dp = DimensUI.AvatarSizeLarge,
    backgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer
) {
    AppAvatar(
        name = name,
        modifier = modifier,
        image = image,
        size = size,
        backgroundColor = backgroundColor,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        textStyle = MaterialTheme.typography.titleLarge,
        description = "Fotografía de $name"
    )
}
