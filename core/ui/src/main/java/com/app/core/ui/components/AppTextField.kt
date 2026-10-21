package com.app.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Campo de texto reutilizable de Mascotas.
 *
 * No administra el estado del formulario:
 * recibe el valor y notifica sus cambios.
 */
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation =
        VisualTransformation.None
) {
    val hasError = isError || !errorMessage.isNullOrBlank()

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        readOnly = readOnly,
        singleLine = singleLine,
        isError = hasError,
        label = {
            Text(text = label)
        },
        placeholder = placeholder?.let { placeholderText ->
            {
                Text(text = placeholderText)
            }
        },
        supportingText = errorMessage
            ?.takeIf { it.isNotBlank() }
            ?.let { message ->
                {
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        visualTransformation = visualTransformation,
        shape = MaterialTheme.shapes.medium
    )
}