
package com.app.features.walkers.list.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.app.core.ui.components.AppAnimatedVisibility
import com.app.core.ui.components.AppCard
import com.app.core.ui.components.AppCheckboxRow
import com.app.core.ui.components.AppFilterChip
import com.app.core.ui.components.AppTextField
import com.app.core.ui.components.StatusBadge
import com.app.core.ui.components.TextActionButton
import com.app.core.ui.theme.DimensUI

@Composable
fun WalkerFilterSection(
    state: WalkerFilterState,
    resultCount: Int,
    onStateChange: (WalkerFilterState) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }

    AppCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                DimensUI.SpaceMedium
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AppFilterChip(
                    text = if (expanded) {
                        "Ocultar filtros"
                    } else {
                        "Filtrar paseadores"
                    },
                    selected = expanded,
                    onClick = {
                        expanded = !expanded
                    }
                )

                StatusBadge(
                    text = when (resultCount) {
                        1 -> "1 resultado"
                        else -> "$resultCount resultados"
                    }
                )
            }

            if (state.hasActiveFilters) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(
                        DimensUI.SpaceSmall
                    )
                ) {
                    Text(
                        text = "Filtros aplicados",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(
                            DimensUI.SpaceSmall
                        )
                    ) {
                        if (state.serviceArea.isNotBlank()) {
                            AppFilterChip(
                                text = "Zona: ${state.serviceArea}",
                                selected = true,
                                onClick = {
                                    onStateChange(
                                        state.copy(serviceArea = "")
                                    )
                                }
                            )
                        }

                        if (state.maxPriceText.isNotBlank()) {
                            AppFilterChip(
                                text = "Precio máximo",
                                selected = true,
                                onClick = {
                                    onStateChange(
                                        state.copy(maxPriceText = "")
                                    )
                                }
                            )
                        }

                        if (state.onlyAvailable) {
                            AppFilterChip(
                                text = "Disponibles",
                                selected = true,
                                onClick = {
                                    onStateChange(
                                        state.copy(onlyAvailable = false)
                                    )
                                }
                            )
                        }
                    }
                }
            }

            AppAnimatedVisibility(
                visible = expanded,
                durationMillis = 220
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(
                        DimensUI.SpaceMedium
                    )
                ) {
                    Text(
                        text = "Personalizá tu búsqueda",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    AppTextField(
                        value = state.serviceArea,
                        onValueChange = { value ->
                            onStateChange(
                                state.copy(serviceArea = value)
                            )
                        },
                        label = "Ciudad o zona",
                        placeholder = "Ej.: Quilmes"
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

                    AppCheckboxRow(
                        checked = state.onlyAvailable,
                        onCheckedChange = { checked ->
                            onStateChange(
                                state.copy(onlyAvailable = checked)
                            )
                        },
                        label = "Mostrar solo paseadores disponibles",
                        description = "Oculta los paseadores que no reciben solicitudes."
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextActionButton(
                            text = "Limpiar filtros",
                            onClick = {
                                onStateChange(state.clear())
                            },
                            enabled = state.hasActiveFilters
                        )
                    }
                }
            }
        }
    }
}
