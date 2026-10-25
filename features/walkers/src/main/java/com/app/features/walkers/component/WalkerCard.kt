package com.app.features.walkers.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.app.core.domain.model.Walker
import com.app.core.ui.theme.DimensUI
import com.app.core.ui.theme.MascotasThemeUI
import java.text.NumberFormat
import java.util.Locale

@Composable
fun WalkerCard(
    walker: Walker,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val priceFormatter = NumberFormat.getCurrencyInstance(
        Locale("es", "AR")
    )

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = DimensUI.SpaceXXSmall
        )
    ) {
        Column(
            modifier = Modifier.padding(DimensUI.SpaceLarge),
            verticalArrangement = Arrangement.spacedBy(
                DimensUI.SpaceMedium
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
                        text = walker.serviceArea.ifBlank {
                            "Zona no especificada"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = walker.description.ifBlank {
                            "Sin descripción"
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
                        text = "EXPERIENCIA",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "${walker.experienceYears} años",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }


                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(
                        DimensUI.SpaceXSmall
                    )
                ) {
                    Text(
                        text = "PRECIO POR PASEO",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = priceFormatter.format(walker.pricePerWalk),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Text(
                text = "Máximo de mascotas: ${walker.maxPets}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = if (walker.available) {
                    "Disponible"
                } else {
                    "No disponible"
                },
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WalkerCardPreview() {
    val walker = Walker(
        userId = "USR001",
        description = "Paseos y cuidado responsable de mascotas",
        experienceYears = 3,
        serviceArea = "Quilmes",
        available = true,
        pricePerWalk = 8000.0,
        maxPets = 3,
        rating = 4.8
    )

    MascotasThemeUI {
        WalkerCard(
            walker = walker,
            modifier = Modifier.padding(DimensUI.SpaceMedium)
        )
    }
}