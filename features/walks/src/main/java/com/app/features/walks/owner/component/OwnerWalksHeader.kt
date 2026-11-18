package com.app.features.walks.owner.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.app.core.ui.theme.MascotasThemeUI
import com.app.core.ui.components.SectionHeader

@Composable
fun OwnerWalksHeader() {
    SectionHeader(
        title = "Mis paseos",
        subtitle = "Consultá tus solicitudes, próximos paseos e historial."
    )
}

@Preview(showBackground = true)
@Composable
private fun OwnerWalksHeaderPreview() {
    MascotasThemeUI {
        OwnerWalksHeader()
    }
}