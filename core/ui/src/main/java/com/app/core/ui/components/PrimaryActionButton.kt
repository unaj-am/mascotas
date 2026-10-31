
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
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import com.app.core.ui.theme.DimensUI
import com.app.core.ui.theme.ShapeUI

@Composable
fun PrimaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    fullWidth: Boolean = true
) {
    val buttonModifier = modifier
        .then(
            if (fullWidth) Modifier.fillMaxWidth()
            else Modifier
        )
        .heightIn(min = DimensUI.ButtonHeight)

    Button(
        onClick = onClick,
        modifier = buttonModifier,
        enabled = enabled && !isLoading,
        shape = ShapeUI.Button,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = DimensUI.SpaceXSmall,
            pressedElevation = DimensUI.SpaceXXSmall,
            disabledElevation = DimensUI.SpaceNone
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor =
                MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor =
                MaterialTheme.colorScheme.onSurfaceVariant
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
                    modifier = Modifier
                        .size(DimensUI.IconSizeSmall)
                        .semantics {
                            progressBarRangeInfo =
                                ProgressBarRangeInfo.Indeterminate
                        },
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
