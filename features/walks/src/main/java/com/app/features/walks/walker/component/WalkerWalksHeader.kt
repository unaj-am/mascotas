
package com.app.features.walks.walker.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.app.core.ui.components.SectionHeader
import com.app.core.ui.components.TextActionButton

@Composable
fun WalkerWalksHeader(
    title: String,
    subtitle: String,
    actionText: String,
    isActionEnabled: Boolean = true,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SectionHeader(
            title = title,
            subtitle = subtitle,
            modifier = Modifier.weight(1f)
        )

        TextActionButton(
            text = actionText,
            onClick = onAction,
            enabled = isActionEnabled
        )
    }
}
