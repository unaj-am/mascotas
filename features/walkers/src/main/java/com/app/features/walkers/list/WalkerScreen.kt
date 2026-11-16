package com.app.features.walkers.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.core.domain.model.Walker
import com.app.core.ui.components.AppAnimatedVisibility
import com.app.core.ui.theme.DimensUI
import com.app.core.ui.theme.MascotasThemeUI
import com.app.features.walkers.list.component.WalkerContent
import com.app.features.walkers.list.component.WalkerFilterSection
import com.app.features.walkers.list.component.WalkerFilterState
import com.app.features.walkers.list.component.WalkerHeader

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

    val filterState = WalkerFilterState(
        serviceArea = serviceArea,
        maxPriceText = maxPriceText,
        onlyAvailable = onlyAvailable
    )
    val displayState = filterState.applyTo(uiState)

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = DimensUI.ScreenVerticalPadding),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 1120.dp)
                .fillMaxSize()
                .padding(horizontal = DimensUI.ScreenHorizontalPadding),
            verticalArrangement = Arrangement.spacedBy(
                DimensUI.SectionSpacingCompact
            )
        ) {
            AppAnimatedVisibility(
                visible = true,
                durationMillis = 260
            ) {
                WalkerHeader(uiState = displayState)
            }

            if (uiState is WalkerUiState.Success) {
                val resultCount = (displayState as? WalkerUiState.Success)
                    ?.walkers
                    ?.size
                    ?: 0

                AppAnimatedVisibility(
                    visible = true,
                    durationMillis = 300
                ) {
                    WalkerFilterSection(
                        state = filterState,
                        resultCount = resultCount,
                        onStateChange = { newState ->
                            serviceArea = newState.serviceArea
                            maxPriceText = newState.maxPriceText
                            onlyAvailable = newState.onlyAvailable
                        }
                    )
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                AppAnimatedVisibility(
                    visible = true,
                    modifier = Modifier.fillMaxSize(),
                    durationMillis = 340
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
    }
}

@Preview(showBackground = true)
@Composable
private fun WalkerScreenPreview() {
    val walkers = listOf(
        Walker(
            userId = "USR001",
            description = "Paseos y cuidado responsable de mascotas",
            experienceYears = 3,
            serviceArea = "Quilmes",
            available = true,
            pricePerWalk = 8000.0,
            maxPets = 3,
            rating = 4.8
        ),
        Walker(
            userId = "USR002",
            description = "Experiencia con perros de todos los tamaños",
            experienceYears = 5,
            serviceArea = "Bernal",
            available = true,
            pricePerWalk = 9500.0,
            maxPets = 4,
            rating = 4.9
        )
    )

    MascotasThemeUI {
        WalkerScreen(uiState = WalkerUiState.Success(walkers))
    }
}

@Preview(
    name = "Búsqueda en ventana amplia",
    showBackground = true,
    widthDp = 840,
    heightDp = 1200
)
@Composable
private fun WalkerScreenWidePreview() {
    WalkerScreenPreview()
}
