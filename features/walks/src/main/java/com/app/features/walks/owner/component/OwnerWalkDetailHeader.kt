package com.app.features.walks.owner.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.app.core.ui.components.SectionHeader

@Composable
fun OwnerWalkDetailHeader(
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SectionHeader(
            title = "Detalle del paseo",
            subtitle = "Consultá la información y el estado actual.",
            modifier = Modifier.weight(1f)
        )

        OutlinedButton(
            onClick = onBack
        ) {
            Text("Volver")
        }
    }
}