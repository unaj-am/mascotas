
package com.app.features.pets.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.app.core.domain.model.Pet
import com.app.core.ui.components.AppAvatar
import com.app.core.ui.components.AppCard
import com.app.core.ui.components.InfoRow
import com.app.core.ui.components.StatusBadge
import com.app.core.ui.theme.DimensUI
import com.app.core.ui.theme.MascotasThemeUI

@Composable
fun PetCard(
    pet: Pet,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        contentPadding = DimensUI.CardPaddingLarge
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
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!pet.photoUri.isNullOrBlank()) {
                    AsyncImage(
                        model = pet.photoUri,
                        contentDescription = "Foto de ${pet.name}",
                        modifier = Modifier
                            .size(DimensUI.AvatarSizeXLarge)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    AppAvatar(
                        name = pet.name,
                        size = DimensUI.AvatarSizeXLarge,
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        description = "Avatar de ${pet.name}"
                    )
                }

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

                    if (pet.size.isNotBlank()) {
                        StatusBadge(
                            text = pet.size
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "Ver detalle de ${pet.name}",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
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
                    value = if (pet.age == 1) {
                        "1 año"
                    } else {
                        "${pet.age} años"
                    },
                    modifier = Modifier.weight(1f)
                )

                InfoRow(
                    label = "Peso",
                    value = "${pet.weight} kg",
                    modifier = Modifier.weight(1f)
                )
            }

            if (pet.behavior.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        DimensUI.SpaceSmall
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Pets,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(
                            DimensUI.IconSizeSmall
                        )
                    )

                    Text(
                        text = pet.behavior,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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
        weight = 25.0,
        size = "Grande",
        behavior = "Sociable"
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
