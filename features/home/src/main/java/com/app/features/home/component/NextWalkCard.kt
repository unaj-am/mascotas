package com.app.features.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.app.core.ui.theme.DimensUI
import com.app.core.ui.theme.PetOwnerPurple
import com.app.core.ui.theme.PetTextOnDark

@Composable
fun NextWalkCard(
    petName: String?,
    petBreed: String?,
    walkDate: String?,
    onFindWalkerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SpaceMedium
        )
    ) {
        HomeSectionHeader(
            title = "PRÓXIMO PASEO"
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = PetOwnerPurple
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = DimensUI.SpaceXSmall
            )
        ) {
            Column(
                modifier = Modifier.padding(DimensUI.SpaceXLarge),
                verticalArrangement = Arrangement.spacedBy(
                    DimensUI.SpaceMedium
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        DimensUI.SpaceSmall
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.WatchLater,
                        contentDescription = null,
                        tint = PetTextOnDark
                    )

                    Text(
                        text = "Paseo programado",
                        style = MaterialTheme.typography.labelLarge,
                        color = PetTextOnDark
                    )
                }

                if (!petName.isNullOrBlank()) {
                    Text(
                        text = petName,
                        style = MaterialTheme.typography.headlineMedium,
                        color = PetTextOnDark
                    )

                    if (!petBreed.isNullOrBlank()) {
                        Text(
                            text = petBreed,
                            style = MaterialTheme.typography.bodyMedium,
                            color = PetTextOnDark
                        )
                    }

                    if (!walkDate.isNullOrBlank()) {
                        Text(
                            text = walkDate,
                            style = MaterialTheme.typography.bodyMedium,
                            color = PetTextOnDark
                        )
                    }
                } else {
                    Text(
                        text = "Todavía no tenés paseos programados",
                        style = MaterialTheme.typography.titleMedium,
                        color = PetTextOnDark
                    )

                    Text(
                        text = "Buscá un paseador para organizar el próximo.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PetTextOnDark
                    )
                }

                Surface(
                    onClick = onFindWalkerClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier.padding(
                            DimensUI.SpaceMedium
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Buscar paseador",
                            style = MaterialTheme.typography.labelLarge,
                            color = PetOwnerPurple
                        )

                        Spacer(
                            modifier = Modifier.size(
                                DimensUI.SpaceSmall
                            )
                        )

                        Icon(
                            imageVector = Icons.Filled.ArrowForward,
                            contentDescription = null,
                            tint = PetOwnerPurple
                        )
                    }
                }
            }
        }
    }
}