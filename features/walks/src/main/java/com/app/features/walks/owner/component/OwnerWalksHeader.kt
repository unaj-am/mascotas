package com.app.features.walks.owner.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.ui.components.SectionHeader

@Composable
fun OwnerWalksHeader(
    onRefresh: () -> Unit
) {
    SectionHeader(
        title = "Mis paseos",
        subtitle = "Consultá tus solicitudes, próximos paseos e historial.",
        actionText = "Actualizar",
        onAction = onRefresh
    )
}