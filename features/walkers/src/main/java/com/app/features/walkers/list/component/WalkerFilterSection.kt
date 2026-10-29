package com.app.features.walkers.list.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.app.core.ui.components.AppCard
import com.app.core.ui.components.AppTextField
import com.app.core.ui.components.StatusBadge
import com.app.core.ui.theme.DimensUI

/**
 * Sección de filtros del listado de paseadores.
 *
 * No carga datos ni accede al ViewModel.
 * Recibe el estado actual y comunica las modificaciones
 * mediante onStateChange.
 */
@Composable
fun WalkerFilterSection(
    state: WalkerFilterState,
    resultCount: Int,
    onStateChange: (WalkerFilterState) -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                DimensUI.SpaceMedium
            )
        ) {
            Text(
                text = "Filtrar paseadores",
                style = MaterialTheme.typography.titleMedium
            )

            AppTextField(
                value = state.serviceArea,
                onValueChange = { value ->
                    onStateChange(
                        state.copy(serviceArea = value)
                    )
                },
                label = "Ciudad o zona",
                placeholder = "Ej.: Chilecito"
            )

            AppTextField(
                value = state.maxPriceText,
                onValueChange = { value ->
                    if (
                        value.isEmpty() ||
                        value.matches(
                            Regex("^\\d*([.,]\\d*)?$")
                        )
                    ) {
                        onStateChange(
                            state.copy(maxPriceText = value)
                        )
                    }
                },
                label = "Precio máximo por paseo",
                placeholder = "Ej.: 5000",
                keyboardType = KeyboardType.Decimal,
                errorMessage = if (state.isPriceInvalid) {
                    "Ingresá un precio válido."
                } else {
                    null
                }
            )

            Text(
                text = "Dejá vacío el campo para no limitar el precio.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = state.onlyAvailable,
                    onCheckedChange = { checked ->
                        onStateChange(
                            state.copy(onlyAvailable = checked)
                        )
                    }
                )

                Text(
                    text = "Mostrar solo disponibles",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(
                    text = when (resultCount) {
                        1 -> "1 resultado"
                        else -> "$resultCount resultados"
                    }
                )

                TextButton(
                    onClick = {
                        onStateChange(state.clear())
                    },
                    enabled = state.hasActiveFilters
                ) {
                    Text("Limpiar filtros")
                }
            }
        }
    }
}