package com.app.features.pets.detail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.domain.model.Pet
import com.app.core.ui.theme.DimensUI

@Composable
fun PetDetailContent(
    pet: Pet,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SpaceMedium
        )
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = DimensUI.SpaceXSmall
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(DimensUI.SpaceLarge),
                verticalArrangement = Arrangement.spacedBy(
                    DimensUI.SpaceLarge
                )
            ) {
                // Foto o mensaje de ausencia de foto.
                PetPhoto(
                    photoUri = pet.photoUri
                )

                // Cabecera de la mascota.
                Column(
                    verticalArrangement = Arrangement.spacedBy(
                        DimensUI.SpaceXSmall
                    )
                ) {
                    Text(
                        text = pet.name,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = pet.breed.ifBlank {
                            "Raza no especificada"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "Información general",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        DimensUI.SpaceLarge
                    )
                ) {
                    PetDetailField(
                        label = "Edad",
                        value = "${pet.age} años",
                        modifier = Modifier.weight(1f)
                    )

                    PetDetailField(
                        label = "Peso",
                        value = "${pet.weight} kg",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        DimensUI.SpaceLarge
                    )
                ) {
                    PetDetailField(
                        label = "Tamaño",
                        value = pet.size.ifBlank {
                            "No especificado"
                        },
                        modifier = Modifier.weight(1f)
                    )

                    PetDetailField(
                        label = "Comportamiento",
                        value = pet.behavior.ifBlank {
                            "No especificado"
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                PetDetailField(
                    label = "Necesidades especiales",
                    value = pet.specialNeeds.ifBlank {
                        "Ninguna registrada"
                    }
                )

                PetDetailField(
                    label = "Observaciones",
                    value = pet.observations.ifBlank {
                        "Sin observaciones"
                    }
                )
            }
        }
    }
}