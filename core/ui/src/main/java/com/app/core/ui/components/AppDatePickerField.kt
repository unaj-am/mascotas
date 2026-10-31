
package com.app.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDatePickerField(
    selectedDateMillis: Long?,
    onDateSelected: (Long?) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Fecha",
    enabled: Boolean = true,
    allowClear: Boolean = false
) {
    var showDatePicker by rememberSaveable {
        mutableStateOf(false)
    }

    val dateFormatter = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }

    val formattedDate = selectedDateMillis?.let {
        dateFormatter.format(Date(it))
    }.orEmpty()

    OutlinedTextField(
        value = formattedDate,
        onValueChange = {},
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        readOnly = true,
        singleLine = true,
        label = {
            Text(text = label)
        },
        placeholder = {
            Text(text = "Seleccionar fecha")
        },
        trailingIcon = {
            TextButton(
                onClick = {
                    showDatePicker = true
                },
                enabled = enabled
            ) {
                Text("Elegir")
            }
        },
        shape = MaterialTheme.shapes.large
    )

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDateMillis
        )

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDateSelected(
                            datePickerState.selectedDateMillis
                        )
                        showDatePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                if (allowClear && selectedDateMillis != null) {
                    TextButton(
                        onClick = {
                            onDateSelected(null)
                            showDatePicker = false
                        }
                    ) {
                        Text("Limpiar")
                    }
                }

                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = {
                    Text(
                        text = "Seleccionar fecha",
                        modifier = Modifier
                    )
                }
            )
        }
    }
}
