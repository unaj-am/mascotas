
package com.app.features.walks.walker.component

import androidx.compose.runtime.Composable
import com.app.core.ui.components.AppAnimatedVisibility
import com.app.core.ui.components.AppFeedbackType
import com.app.core.ui.components.AppInlineFeedback

@Composable
fun WalkerWalksFeedback(
    operationMessage: String?,
    operationError: String?,
    loadingError: String?,
    onRetry: () -> Unit
) {
    AppAnimatedVisibility(
        visible = !operationMessage.isNullOrBlank()
    ) {
        AppInlineFeedback(
            title = "Operación realizada",
            message = operationMessage.orEmpty(),
            type = AppFeedbackType.SUCCESS
        )
    }

    AppAnimatedVisibility(
        visible = !operationError.isNullOrBlank()
    ) {
        AppInlineFeedback(
            title = "No se pudo completar la operación",
            message = operationError.orEmpty(),
            type = AppFeedbackType.ERROR,
            actionLabel = "Reintentar",
            onAction = onRetry
        )
    }

    AppAnimatedVisibility(
        visible = !loadingError.isNullOrBlank()
    ) {
        AppInlineFeedback(
            title = "No se pudo cargar la información",
            message = loadingError.orEmpty(),
            type = AppFeedbackType.ERROR,
            actionLabel = "Reintentar",
            onAction = onRetry
        )
    }
}
