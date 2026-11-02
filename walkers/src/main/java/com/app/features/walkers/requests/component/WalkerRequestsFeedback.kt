
package com.app.features.walkers.requests.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.ui.components.AppFeedbackType
import com.app.core.ui.components.AppInlineFeedback
import com.app.core.ui.components.DomainErrorMessage
import com.app.core.ui.theme.DimensUI

@Composable
fun WalkerRequestsFeedback(
    operationMessage: String?,
    operationError: String?,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SpaceSmall
        )
    ) {
        operationMessage?.let { message ->
            AppInlineFeedback(
                message = message,
                type = AppFeedbackType.SUCCESS,
                title = "Operación completada"
            )
        }

        operationError?.let { message ->
            AppInlineFeedback(
                message = message,
                type = AppFeedbackType.ERROR,
                title = "No se pudo completar la operación",
                actionLabel = "Reintentar",
                onAction = onRetry
            )
        }
    }
}
