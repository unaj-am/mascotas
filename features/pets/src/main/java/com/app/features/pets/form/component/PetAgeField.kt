
package com.app.features.pets.form.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import com.app.core.ui.components.AppTextField

/**
 * Campo de edad con controles para incrementar
 * y disminuir el valor.
 *
 * La edad se mantiene como String mientras el usuario
 * completa el formulario. La validación final se realiza
 * en PetFormScreen.
 */
@Composable
fun PetAgeField(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    AppTextField(
        value = value,
        onValueChange = { input ->
            onValueChange(input.filter(Char::isDigit))
        },
        label = "Edad en años *",
        keyboardType = KeyboardType.Number,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        trailingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    enabled = enabled &&
                            (value.toIntOrNull() ?: 0) > 0,
                    onClick = {
                        onValueChange(
                            ((value.toIntOrNull() ?: 0) - 1).toString()
                        )
                    },
                    modifier = Modifier.semantics {
                        contentDescription = "Disminuir edad"
                    }
                ) {
                    Text("−")
                }

                TextButton(
                    enabled = enabled,
                    onClick = {
                        onValueChange(
                            ((value.toIntOrNull() ?: 0) + 1).toString()
                        )
                    },
                    modifier = Modifier.semantics {
                        contentDescription = "Aumentar edad"
                    }
                ) {
                    Text("+")
                }
            }
        }
    )
}
