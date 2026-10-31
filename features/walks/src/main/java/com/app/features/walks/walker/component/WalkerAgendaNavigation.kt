
package com.app.features.walks.walker.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.ui.components.SecondaryActionButton
import com.app.core.ui.theme.DimensUI

@Composable
fun WalkerAgendaNavigation(
    onActiveWalkClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            DimensUI.SpaceMedium
        )
    ) {
        SecondaryActionButton(
            text = "Paseos activos",
            onClick = onActiveWalkClick,
            modifier = Modifier.weight(1f)
        )

        SecondaryActionButton(
            text = "Historial",
            onClick = onHistoryClick,
            modifier = Modifier.weight(1f)
        )
    }
}
