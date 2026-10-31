
package com.app.core.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.core.ui.theme.DimensUI

@Composable
fun LocationRow(
    location: String,
    modifier: Modifier = Modifier,
    secondaryText: String? = null,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null
) {
    AppListItem(
        title = location,
        modifier = modifier,
        subtitle = secondaryText,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        titleStyle = MaterialTheme.typography.bodyMedium,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            0.dp
        ),
        horizontalSpacing = DimensUI.SpaceMedium
    )
}
