package com.app.features.walkers.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.core.domain.model.Walker
import com.app.core.ui.theme.DimensUI
import com.app.core.ui.theme.MascotasThemeUI
import com.app.features.walkers.list.component.WalkerContent
import com.app.features.walkers.list.component.WalkerHeader

@Composable
fun WalkerRoute(
    viewModel: WalkerViewModel,
    onWalkerClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WalkerScreen(
        uiState = uiState,
        onWalkerClick = onWalkerClick,
        onRetry = viewModel::loadWalkers,
        modifier = modifier
    )
}

@Composable
fun WalkerScreen(
    uiState: WalkerUiState,
    onWalkerClick: (String) -> Unit = {},
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier
) {
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
        WalkerHeader(uiState = uiState)

        Box(
            modifier = Modifier.weight(1f)
        ) {
            WalkerContent(
                uiState = uiState,
                onWalkerClick = onWalkerClick,
                onRetry = onRetry
            )
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
            serviceArea = "Berazategui",
            available = false,
            pricePerWalk = 10000.0,
            maxPets = 2,
            rating = 4.9
        )
    )

    MascotasThemeUI {
        WalkerScreen(
            uiState = WalkerUiState.Success(walkers)
        )
    }
}