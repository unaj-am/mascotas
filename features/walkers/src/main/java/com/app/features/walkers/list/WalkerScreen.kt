
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.core.ui.theme.DimensUI
import com.app.features.walkers.list.component.WalkerContent
import com.app.features.walkers.list.component.WalkerHeader

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

    val displayState = when (uiState) {
        is WalkerUiState.Success -> {
            val maxPrice = maxPriceText
                .trim()
                .replace(",", ".")
                .toDoubleOrNull()

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

                matchesArea &&
                        matchesPrice &&
                        matchesAvailability
            }

            WalkerUiState.Success(
                walkers = filteredWalkers
            )
        }

        is WalkerUiState.Loading -> WalkerUiState.Loading

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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onBack
            ) {
                Text(
                    text = "← Volver",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        WalkerHeader(
            uiState = displayState
        )

        if (uiState is WalkerUiState.Success) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Filtrar paseadores",
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedTextField(
                    value = serviceArea,
                    onValueChange = { serviceArea = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Ciudad o zona")
                    },
                    placeholder = {
                        Text("Ej.: Chilecito")
                    },
                    singleLine = true
                )

                OutlinedTextField(
                    value = maxPriceText,
                    onValueChange = { value ->
                        if (
                            value.isEmpty() ||
                            value.matches(Regex("^\\d*([.,]\\d*)?$"))
                        ) {
                            maxPriceText = value
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Precio máximo por paseo")
                    },
                    placeholder = {
                        Text("Ej.: 5000")
                    },
                    supportingText = {
                        Text("Dejá vacío el campo para no limitar el precio.")
                    },
                    singleLine = true
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
                        text = "Mostrar solo paseadores disponibles",
                        style = MaterialTheme.typography.bodyMedium
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

                if (
                    maxPriceText.isNotBlank() &&
                    maxPriceText.replace(",", ".").toDoubleOrNull() == null
                ) {
                    Text(
                        text = "Ingresá un precio válido.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (displayState is WalkerUiState.Success) {
                    Text(
                        text = if (displayState.walkers.size == 1) {
                            "1 paseador encontrado"
                        } else {
                            "${displayState.walkers.size} paseadores encontrados"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
                onBack = onBack
            )
        }
    }
}
