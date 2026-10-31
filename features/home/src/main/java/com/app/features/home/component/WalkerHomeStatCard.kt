
package com.app.features.home.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.ui.components.StatCard

@Composable
fun WalkerHomeStatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    StatCard(
        title = title,
        value = value,
        modifier = modifier
    )
}
