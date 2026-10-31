
package com.app.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.ui.theme.ShapeUI

@Composable
fun DomainErrorMessage(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null
) {
    if (message.isBlank()) return

    AppInlineFeedback(
        message = message,
        type = AppFeedbackType.ERROR,
        modifier = modifier,
        title = "No pudimos completar la operación",
        actionLabel = if (onRetry != null) "Reintentar" else null,
        onAction = onRetry,
        shape = ShapeUI.Medium
    )
}
