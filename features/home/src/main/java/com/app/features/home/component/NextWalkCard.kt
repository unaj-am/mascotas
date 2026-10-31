
package com.app.features.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import com.app.core.ui.components.AppCard
import com.app.core.ui.components.PrimaryActionButton
import com.app.core.ui.theme.DimensUI
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
            title = "Próximo paseo"
        )

        AppCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = DimensUI.CardPaddingLarge
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    DimensUI.SpaceLarge
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        DimensUI.SpaceSmall
                    )
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier.size(44.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.WatchLater,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Text(
                        text = "Paseo programado",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (!petName.isNullOrBlank()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(
                            DimensUI.SpaceXSmall
                        )
                    ) {
                        Text(
                            text = petName,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (!petBreed.isNullOrBlank()) {
                            Text(
                                text = petBreed,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (!walkDate.isNullOrBlank()) {
                            Spacer(
                                modifier = Modifier.size(
                                    DimensUI.SpaceXSmall
                                )
                            )

                            Text(
                                text = walkDate,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(
                            DimensUI.SpaceXSmall
                        )
                    ) {
                        Text(
                            text = "Todavía no tenés paseos programados",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Buscá un paseador para organizar el próximo.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                PrimaryActionButton(
                    text = "Buscar paseador",
                    onClick = onFindWalkerClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
