
package com.app.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.ui.theme.DimensUI
import com.app.core.ui.theme.ShapeUI

@Composable
fun SecondaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fullWidth: Boolean = true
) {
    val buttonModifier = modifier
        .then(
            if (fullWidth) Modifier.fillMaxWidth()
            else Modifier
        )
        .heightIn(min = DimensUI.ButtonHeight)

    FilledTonalButton(
        onClick = onClick,
        modifier = buttonModifier,
        enabled = enabled,
        shape = ShapeUI.Button,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor =
                MaterialTheme.colorScheme.secondaryContainer,
            contentColor =
                MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
