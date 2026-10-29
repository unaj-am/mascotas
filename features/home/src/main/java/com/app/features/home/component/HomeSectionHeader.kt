
package com.app.features.home.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.app.core.ui.theme.DimensUI

@Composable
fun HomeSectionHeader(
    title: String,
    action: String? = null,
    onAction: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.5.sp,
            fontWeight = FontWeight.SemiBold
        )

        if (action != null) {
            TextButton(
                onClick = onAction,
                contentPadding = PaddingValues(
                    horizontal = DimensUI.SpaceSmall,
                    vertical = DimensUI.SpaceXSmall
                )
            ) {
                Text(
                    text = action,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
