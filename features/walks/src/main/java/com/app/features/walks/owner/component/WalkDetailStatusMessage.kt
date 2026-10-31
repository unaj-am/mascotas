
package com.app.features.walks.owner.component

import androidx.compose.runtime.Composable
import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkStatus
import com.app.core.ui.components.AppFeedbackType
import com.app.core.ui.components.AppInlineFeedback

@Composable
fun WalkDetailStatusMessage(
    walk: Walk,
    onTrackWalk: (String) -> Unit
) {
    when (walk.status) {
        WalkStatus.IN_PROGRESS -> {
            AppInlineFeedback(
                title = "Paseo en curso",
                message = "El paseador inició el recorrido. Podés consultar su seguimiento.",
                type = AppFeedbackType.INFO,
                actionLabel = "Ver seguimiento",
                onAction = {
                    onTrackWalk(walk.id)
                }
            )
        }

        WalkStatus.SCHEDULED -> {
            AppInlineFeedback(
                title = "Paseo confirmado",
                message = "El paseo está confirmado. El seguimiento estará disponible cuando comience.",
                type = AppFeedbackType.INFO
            )
        }

        WalkStatus.COMPLETED -> {
            AppInlineFeedback(
                title = "Paseo finalizado",
                message = "Este paseo ya finalizó. Podés consultar los datos registrados.",
                type = AppFeedbackType.SUCCESS
            )
        }

        WalkStatus.CANCELLED -> {
            AppInlineFeedback(
                title = "Paseo cancelado",
                message = "Este paseo fue cancelado.",
                type = AppFeedbackType.ERROR
            )
        }
    }
}
