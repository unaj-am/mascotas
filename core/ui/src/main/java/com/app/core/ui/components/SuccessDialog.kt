
package com.app.core.ui.components

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun SuccessDialog(
    title: String,
    message: String,
    confirmText: String = "Aceptar",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit = onConfirm
) {
    AppDialog(
        visible = true,
        onDismiss = onDismiss,
        title = title,
        text = message,
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(confirmText)
            }
        }
    )
}
