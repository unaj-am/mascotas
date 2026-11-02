
package com.app.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.app.core.ui.theme.PetInfo
import com.app.core.ui.theme.PetInfoContainer

enum class AppFeedbackType {
    INFO,
    SUCCESS,
    WARNING,
    ERROR
}

@Composable
fun AppInlineFeedback(
    message: String,
    type: AppFeedbackType,
    modifier: Modifier = Modifier,
    title: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    shape: Shape = MaterialTheme.shapes.large
) {
    val containerColor: Color
    val contentColor: Color

    when (type) {
        AppFeedbackType.INFO -> {
            containerColor = MaterialTheme.colorScheme.primaryContainer
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        }

        AppFeedbackType.SUCCESS -> {
            containerColor = PetInfoContainer
            contentColor = PetInfo
        }

        AppFeedbackType.WARNING -> {
            containerColor = MaterialTheme.colorScheme.secondaryContainer
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        }

        AppFeedbackType.ERROR -> {
            containerColor = MaterialTheme.colorScheme.errorContainer
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = containerColor,
        contentColor = contentColor,
        shape = shape
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (!title.isNullOrBlank()) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (actionLabel != null && onAction != null) {
                TextButton(onClick = onAction) {
                    Text(
                        text = actionLabel,
                        color = contentColor
                    )
                }
            }
        }
    }
}
