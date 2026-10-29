
package com.app.features.walkers.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.core.ui.components.AppCard
import com.app.core.ui.components.AppTextField
import com.app.core.ui.components.StatusBadge
import com.app.core.ui.theme.DimensUI
import com.app.features.walkers.list.component.WalkerContent
import com.app.features.walkers.list.component.WalkerHeader

/**
 * Route del listado de paseadores.
 *
 */
@Composable
fun WalkerRoute(
    viewModel: WalkerViewModel,
    onWalkerClick: (String) -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WalkerScreen(
        uiState = uiState,
        onWalkerClick = onWalkerClick,
        onRetry = viewModel::loadWalkers,
        onBack = onBack,
        modifier = modifier
    )
}

/**
 * Pantalla del listado con filtros locales.
 *
 * Los filtros se aplican sobre los paseadores
 * recibidos en el estado Success.
 */
@Composable
fun WalkerScreen(
    uiState: WalkerUiState,
    onWalkerClick: (String) -> Unit = {},
    onRetry: () -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var serviceArea by rememberSaveable {
        mutableStateOf("")
    }

    var maxPriceText by rememberSaveable {
        mutableStateOf("")
    }

    var onlyAvailable by rememberSaveable {
        mutableStateOf(false)
    }

    val maxPrice = maxPriceText
        .trim()
        .replace(",", ".")
        .toDoubleOrNull()

    val isPriceInvalid =
        maxPriceText.isNotBlank() && maxPrice == null

    val displayState = when (uiState) {
        is WalkerUiState.Success -> {
            val filteredWalkers = uiState.walkers.filter { walker ->
                val matchesArea =
                    serviceArea.isBlank() ||
                            walker.serviceArea.contains(
                                serviceArea.trim(),
                                ignoreCase = true
                            )

                val matchesPrice =
                    maxPrice == null ||
                            walker.pricePerWalk <= maxPrice

                val matchesAvailability =
                    !onlyAvailable || walker.available

                matchesArea && matchesPrice && matchesAvailability
            }

            WalkerUiState.Success(
                walkers = filteredWalkers
            )
        }

        WalkerUiState.Loading -> WalkerUiState.Loading
        is WalkerUiState.Error -> uiState
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = DimensUI.ScreenHorizontalPadding,
                vertical = DimensUI.ScreenVerticalPadding
            ),
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SectionSpacing
        )
    ) {
        TextButton(
            onClick = onBack
        ) {
            Text("← Volver")
        }

        WalkerHeader(
            uiState = displayState
        )

        if (uiState is WalkerUiState.Success) {
            AppCard(
                modifier = Modifier.fillMaxWidth()
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
                        value = serviceArea,
                        onValueChange = {
                            serviceArea = it
                        },
                        label = "Ciudad o zona",
                        placeholder = "Ej.: Chilecito"
                    )

                    AppTextField(
                        value = maxPriceText,
                        onValueChange = { value ->
                            if (
                                value.isEmpty() ||
                                value.matches(
                                    Regex("^\\d*([.,]\\d*)?$")
                                )
                            ) {
                                maxPriceText = value
                            }
                        },
                        label = "Precio máximo por paseo",
                        placeholder = "Ej.: 5000",
                        keyboardType = KeyboardType.Decimal,
                        errorMessage = if (isPriceInvalid) {
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
                            checked = onlyAvailable,
                            onCheckedChange = {
                                onlyAvailable = it
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
                        if (displayState is WalkerUiState.Success) {
                            StatusBadge(
                                text = if (
                                    displayState.walkers.size == 1
                                ) {
                                    "1 resultado"
                                } else {
                                    "${displayState.walkers.size} resultados"
                                }
                            )
                        }

                        TextButton(
                            onClick = {
                                serviceArea = ""
                                maxPriceText = ""
                                onlyAvailable = false
                            },
                            enabled = serviceArea.isNotBlank() ||
                                    maxPriceText.isNotBlank() ||
                                    onlyAvailable
                        ) {
                            Text("Limpiar filtros")
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier.weight(1f)
        ) {
            WalkerContent(
                uiState = displayState,
                onWalkerClick = onWalkerClick,
                onRetry = onRetry,
                onBack = onBack,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
