package com.app.features.walkers.requests.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.ui.components.AppCard
import com.app.core.ui.components.DomainErrorMessage

@Composable
fun WalkerRequestsFeedback(
    operationMessage: String?,
    operationError: String?,
    onRetry: () -> Unit
) {
    operationMessage?.let { message ->
        AppCard(
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    operationError?.let { message ->
        DomainErrorMessage(
            message = message,
            modifier = Modifier.fillMaxWidth(),
            onRetry = onRetry
        )
    }
}