
package com.app.features.walkers.requestform.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.domain.model.Pet
import com.app.core.ui.components.AppCheckboxRow

@Composable
fun WalkRequestPetItem(
    pet: Pet,
    isSelected: Boolean,
    enabled: Boolean,
    onPetSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    AppCheckboxRow(
        checked = isSelected,
        onCheckedChange = {
            onPetSelected(pet.id)
        },
        label = pet.name,
        modifier = modifier,
        enabled = enabled
    )
}
