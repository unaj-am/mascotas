package com.app.features.walks.walker.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WalkerAgendaNavigation(
    onActiveWalkClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onActiveWalkClick,
            modifier = Modifier.weight(1f)
        ) {
            Text("Paseo activo")
        }

        OutlinedButton(
            onClick = onHistoryClick,
            modifier = Modifier.weight(1f)
        ) {
            Text("Historial")
        }
    }
}