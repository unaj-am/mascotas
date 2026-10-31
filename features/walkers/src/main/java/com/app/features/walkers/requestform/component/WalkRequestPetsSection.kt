
package com.app.features.walkers.requestform.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.app.core.domain.model.Pet
import com.app.core.ui.components.AppCard
import com.app.core.ui.components.EmptyContent
import com.app.core.ui.components.StatusBadge
import com.app.core.ui.theme.DimensUI

@Composable
fun WalkRequestPetsSection(
    pets: List<Pet>,
    selectedPetIds: Set<String>,
    isSubmitting: Boolean,
    onPetSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier.fillMaxWidth(),
        contentPadding = DimensUI.CardPaddingLarge
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                DimensUI.SpaceMedium
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tus mascotas",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                StatusBadge(
                    text = when (selectedPetIds.size) {
                        1 -> "1 seleccionada"
                        else -> "${selectedPetIds.size} seleccionadas"
                    }
                )
            }

            Text(
                text = "Seleccioná una o más mascotas para el paseo.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (pets.isEmpty()) {
                EmptyContent(
                    title = "No tenés mascotas registradas",
                    message = "Necesitás registrar al menos una mascota antes de solicitar un paseo.",
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(
                        DimensUI.SpaceSmall
                    )
                ) {
                    pets.forEach { pet ->
                        WalkRequestPetItem(
                            pet = pet,
                            isSelected = pet.id in selectedPetIds,
                            enabled = !isSubmitting,
                            onPetSelected = onPetSelected
                        )
                    }
                }
            }
        }
    }
}
