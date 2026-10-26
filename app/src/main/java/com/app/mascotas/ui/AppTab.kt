
package com.app.mascotas.ui

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey

data class AppTab(
    val key: NavKey,
    val label: String,
    val icon: ImageVector
)
