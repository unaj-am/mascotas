package com.app.features.walks.owner.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkStatus
import com.app.core.ui.components.AppCard
import com.app.core.ui.components.PrimaryActionButton

@Composable
fun WalkDetailStatusMessage(
    walk: Walk,
    onTrackWalk: (String) -> Unit
) {
    when (walk.status) {
        WalkStatus.IN_PROGRESS -> {
            PrimaryActionButton(
                text = "Consultar seguimiento",
                onClick = {
                    onTrackWalk(walk.id)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        WalkStatus.SCHEDULED -> {
            AppCard {
                Text(
                    text = "El paseo está confirmado. El seguimiento estará disponible cuando comience.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        WalkStatus.COMPLETED -> {
            AppCard {
                Text(
                    text = "Este paseo ya finalizó. Podés consultar los datos registrados.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        WalkStatus.CANCELLED -> {
            AppCard {
                Text(
                    text = "Este paseo fue cancelado.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}