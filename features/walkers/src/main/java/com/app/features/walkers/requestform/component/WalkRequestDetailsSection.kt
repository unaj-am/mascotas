
package com.app.features.walkers.requestform.component

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import com.app.core.ui.components.AppCard
import com.app.core.ui.components.AppFormSection
import com.app.core.ui.components.AppTextField
import com.app.core.ui.theme.DimensUI
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val storageFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")

private val displayFormatter =
    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

@Composable
fun WalkRequestDetailsSection(
    scheduledAt: String,
    durationMinutes: String,
    observations: String,
    isSubmitting: Boolean,
    onScheduledAtChange: (String) -> Unit,
    onDurationChange: (String) -> Unit,
    onObservationsChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val selectedDateTime = remember(scheduledAt) {
        runCatching {
            LocalDateTime.parse(scheduledAt)
        }.getOrNull()
    }

    val displayedDateTime = selectedDateTime?.format(
        displayFormatter
    ).orEmpty()

    fun openDatePicker() {
        val initialDate = selectedDateTime?.toLocalDate()
            ?: LocalDate.now()

        val datePicker = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->

                val selectedDate = LocalDate.of(
                    year,
                    month + 1,
                    dayOfMonth
                )

                val initialTime = selectedDateTime?.toLocalTime()
                    ?: LocalTime.of(9, 0)

                TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->

                        val selectedDateTime = LocalDateTime.of(
                            selectedDate,
                            LocalTime.of(hourOfDay, minute)
                        )

                        onScheduledAtChange(
                            selectedDateTime.format(storageFormatter)
                        )
                    },
                    initialTime.hour,
                    initialTime.minute,
                    true
                ).show()
            },
            initialDate.year,
            initialDate.monthValue - 1,
            initialDate.dayOfMonth
        )

        // No permitir seleccionar fechas anteriores a hoy.
        datePicker.datePicker.minDate =
            System.currentTimeMillis()

        datePicker.show()
    }

    AppCard(
        modifier = modifier.fillMaxWidth(),
        contentPadding = DimensUI.CardPaddingLarge
    ) {
        AppFormSection(
            title = "Datos del paseo",
            description = "Indicá cuándo y durante cuánto tiempo necesitás el servicio."
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    DimensUI.SpaceLarge
                )
            ) {

                OutlinedTextField(
                    value = displayedDateTime,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    enabled = !isSubmitting,
                    label = {
                        Text("Fecha y hora")
                    },
                    placeholder = {
                        Text("Seleccionar fecha y hora")
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = ::openDatePicker,
                            enabled = !isSubmitting
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Seleccionar fecha y hora"
                            )
                        }
                    },
                    supportingText = {
                        Text(
                            text = "Seleccioná primero la fecha y después el horario."
                        )
                    },
                    singleLine = true
                )

                AppTextField(
                    value = durationMinutes,
                    onValueChange = { value ->
                        if (value.all(Char::isDigit)) {
                            onDurationChange(value)
                        }
                    },
                    label = "Duración en minutos",
                    placeholder = "60",
                    keyboardType = KeyboardType.Number,
                    enabled = !isSubmitting
                )

                AppTextField(
                    value = observations,
                    onValueChange = onObservationsChange,
                    label = "Observaciones",
                    placeholder = "Indicá cuidados o información relevante para el paseo.",
                    singleLine = false,
                    enabled = !isSubmitting
                )
            }
        }
    }
}
