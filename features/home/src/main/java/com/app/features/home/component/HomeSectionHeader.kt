
package com.app.features.home.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.ui.components.SectionHeader

@Composable
fun HomeSectionHeader(
    title: String,
    action: String? = null,
    onAction: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    SectionHeader(
        title = title,
        modifier = modifier,
        actionText = action,
        onAction = if (action != null) onAction else null
    )
}
