
package com.app.core.ui.components

import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import com.app.core.ui.theme.ShapeUI

object AppFieldDefaults {

    val shape = ShapeUI.TextField

    @Composable
    fun colors(): TextFieldColors {
        return OutlinedTextFieldDefaults.colors(
            focusedBorderColor =
                androidx.compose.material3.MaterialTheme.colorScheme.primary,

            unfocusedBorderColor =
                androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant,

            focusedLabelColor =
                androidx.compose.material3.MaterialTheme.colorScheme.primary,

            unfocusedLabelColor =
                androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,

            cursorColor =
                androidx.compose.material3.MaterialTheme.colorScheme.primary,

            errorBorderColor =
                androidx.compose.material3.MaterialTheme.colorScheme.error,

            errorLabelColor =
                androidx.compose.material3.MaterialTheme.colorScheme.error,

            focusedContainerColor =
                androidx.compose.material3.MaterialTheme.colorScheme.surface,

            unfocusedContainerColor =
                androidx.compose.material3.MaterialTheme.colorScheme.surface,

            disabledContainerColor =
                androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
