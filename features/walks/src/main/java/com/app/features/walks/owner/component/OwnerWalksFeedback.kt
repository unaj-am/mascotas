
package com.app.features.walks.owner.component

import androidx.compose.runtime.Composable
import com.app.core.ui.components.AppFeedbackType
import com.app.core.ui.components.AppInlineFeedback
import com.app.core.ui.components.DomainErrorMessage

@Composable
fun OwnerWalksFeedback(
    operationMessage: String?,
    operationError: String?,
    loadingError: String?,
    onRetry: () -> Unit
) {
    operationMessage?.let { message ->
        AppInlineFeedback(
            title = "Operación completada",
            message = message,
            type = AppFeedbackType.SUCCESS
        )
    }

    operationError?.let { error ->
        AppInlineFeedback(
            title = "No se pudo completar la operación",
            message = error,
            type = AppFeedbackType.ERROR
        )
    }

    loadingError?.let { error ->
        DomainErrorMessage(
            message = error,
            onRetry = onRetry
        )
    }
}
