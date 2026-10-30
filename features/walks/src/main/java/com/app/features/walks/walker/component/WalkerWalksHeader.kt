package com.app.features.walks.walker.component

import androidx.compose.runtime.Composable
import com.app.core.ui.components.SectionHeader

@Composable
fun WalkerWalksHeader(
    title: String,
    subtitle: String,
    onRefresh: () -> Unit
) {
    SectionHeader(
        title = title,
        subtitle = subtitle,
        actionText = "Actualizar",
        onAction = onRefresh
    )
}