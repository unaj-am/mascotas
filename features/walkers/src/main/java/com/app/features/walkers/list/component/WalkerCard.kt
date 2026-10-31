package com.app.features.walkers.list.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.app.core.domain.model.Walker
import com.app.core.ui.components.AppCard
import com.app.core.ui.components.InfoRow
import com.app.core.ui.components.StatusBadge
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

    AppCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                DimensUI.SpaceMedium
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    DimensUI.SpaceSmall
                ),
                verticalAlignment = Alignment.Top
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

                StatusBadge(
                    text = if (walker.available) {
                        "Disponible"
                    } else {
                        "No disponible"
                    },
                    containerColor = if (walker.available) {
                        MaterialTheme.colorScheme.tertiaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    contentColor = if (walker.available) {
                        MaterialTheme.colorScheme.onTertiaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    DimensUI.SpaceLarge
                )
            ) {
                InfoRow(
                    label = "Experiencia",
                    value = if (walker.experienceYears == 1) {
                        "1 año"
                    } else {
                        "${walker.experienceYears} años"
                    },
                    modifier = Modifier.weight(1f)
                )

                InfoRow(
                    label = "Capacidad",
                    value = "Hasta ${walker.maxPets} mascotas",
                    modifier = Modifier.weight(1f)
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(DimensUI.SpaceMedium),
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = DimensUI.SpaceLarge,
                        vertical = DimensUI.SpaceMedium
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(
                            DimensUI.SpaceXXSmall
                        )
                    ) {
                        Text(
                            text = "Precio por paseo",
                            style = MaterialTheme.typography.labelMedium
                        )

                        Text(
                            text = priceFormatter.format(
                                walker.pricePerWalk
                            ),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    val rating = walker.rating

                    if (rating != null && rating > 0.0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(
                                DimensUI.SpaceXSmall
                            )
                        ) {
                            Text(
                                text = "★",
                                color = MaterialTheme.colorScheme.tertiary,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = String.format(
                                    Locale("es", "AR"),
                                    "%.1f / 5",
                                    rating
                                ),
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                }
            }
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
            walker = walker
        )
    }
}
