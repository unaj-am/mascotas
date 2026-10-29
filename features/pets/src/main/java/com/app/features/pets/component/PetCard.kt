
package com.app.features.pets.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.app.core.domain.model.Pet
import com.app.core.ui.components.AppCard
import com.app.core.ui.components.InfoRow
import com.app.core.ui.theme.DimensUI
import com.app.core.ui.theme.MascotasThemeUI

/**
 * Tarjeta específica de una mascota.
 *
 * Utiliza AppCard como contenedor visual e InfoRow
 * para mostrar sus datos principales.
 *
 * La navegación se delega al callback onClick.
 */
@Composable
fun PetCard(
    pet: Pet,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                DimensUI.SpaceLarge
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    DimensUI.SpaceMedium
                )
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(
                        DimensUI.SpaceXSmall
                    )
                ) {
                    Text(
                        text = pet.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = pet.breed.ifBlank {
                            "Raza no especificada"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "→",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    DimensUI.SpaceLarge
                )
            ) {
                InfoRow(
                    label = "Edad",
                    value = "${pet.age} años",
                    modifier = Modifier.weight(1f)
                )

                InfoRow(
                    label = "Peso",
                    value = "${pet.weight} kg",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PetCardPreview() {
    val mascota = Pet(
        id = "PET001",
        ownerId = "USR001",
        name = "Firulais",
        breed = "Labrador",
        age = 3,
        weight = 25.0
    )

    MascotasThemeUI {
        PetCard(
            pet = mascota,
            modifier = Modifier.padding(
                DimensUI.SpaceMedium
            )
        )
    }
}
