package com.app.features.pets.form.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun PetAgeField(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = { input ->
            onValueChange(input.filter(Char::isDigit))
        },
        label = { Text("Edad en años") },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        singleLine = true,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        trailingIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    enabled = enabled && (value.toIntOrNull() ?: 0) > 0,
                    onClick = {
                        onValueChange(
                            ((value.toIntOrNull() ?: 0) - 1).toString()
                        )
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
                    }
                ) {
                    Text("+")
                }
            }
        }
    )
}