
package com.app.core.ui.components

import androidx.compose.material3.FilterChip as MaterialFilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.app.core.ui.theme.ShapeUI

@Composable
fun AppSelectableChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge
) {
    MaterialFilterChip(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = ShapeUI.Chip,
        label = {
            Text(
                text = text,
                style = textStyle
            )
        }
    )
}
