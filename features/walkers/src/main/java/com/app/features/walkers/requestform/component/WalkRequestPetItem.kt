package com.app.features.walkers.requestform.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.app.core.domain.model.Pet
import com.app.core.ui.theme.DimensUI

@Composable
fun WalkRequestPetItem(
    pet: Pet,
    isSelected: Boolean,
    enabled: Boolean,
    onPetSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = {
                onPetSelected(pet.id)
            },
            enabled = enabled
        )

        Text(
            text = pet.name,
            modifier = Modifier
                .weight(1f)
                .padding(start = DimensUI.SpaceSmall),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}