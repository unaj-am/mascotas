package com.app.features.walks.walker.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.app.core.ui.components.DomainErrorMessage

@Composable
fun WalkerWalksFeedback(
    operationMessage: String?,
    operationError: String?,
    loadingError: String?,
    onRetry: () -> Unit
) {
    operationMessage?.let { message ->
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }

    operationError?.let { error ->
        DomainErrorMessage(
            message = error,
            onRetry = onRetry
        )
    }

    loadingError?.let { error ->
        DomainErrorMessage(
            message = error,
            onRetry = onRetry
        )
    }
}