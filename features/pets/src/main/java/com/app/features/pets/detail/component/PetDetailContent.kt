
package com.app.features.pets.detail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.domain.model.Pet
import com.app.core.ui.components.AppCard
import com.app.core.ui.components.InfoRow
import com.app.core.ui.theme.DimensUI

/**
 * Presenta los datos detallados de una mascota.
 *
 * Reutiliza AppCard como contenedor e InfoRow
 * para mantener consistente la presentación de datos.
 */
@Composable
fun PetDetailContent(
    pet: Pet,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SpaceLarge
        )
    ) {
        AppCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    DimensUI.SpaceLarge
                )
            ) {
                PetPhoto(
                    photoUri = pet.photoUri
                )

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
            }
        }

        AppCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    DimensUI.SpaceLarge
                )
            ) {
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        DimensUI.SpaceLarge
                    )
                ) {
                    InfoRow(
                        label = "Tamaño",
                        value = pet.size.ifBlank {
                            "No especificado"
                        },
                        modifier = Modifier.weight(1f)
                    )

                    InfoRow(
                        label = "Comportamiento",
                        value = pet.behavior.ifBlank {
                            "No especificado"
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        AppCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    DimensUI.SpaceLarge
                )
            ) {
                Text(
                    text = "Información adicional",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                InfoRow(
                    label = "Necesidades especiales",
                    value = pet.specialNeeds.ifBlank {
                        "Ninguna registrada"
                    }
                )

                InfoRow(
                    label = "Observaciones",
                    value = pet.observations.ifBlank {
                        "Sin observaciones"
                    }
                )
            }
        }
    }
}
