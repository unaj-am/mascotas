
package com.app.core.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SelectableChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    AppSelectableChip(
        text = text,
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        textStyle = MaterialTheme.typography.labelMedium
    )
}
