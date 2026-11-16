package com.app.features.walkers.list.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.core.domain.model.Walker
import com.app.core.ui.theme.DimensUI
import com.app.core.ui.theme.MascotasThemeUI

@Composable
fun WalkerList(
    walkers: List<Walker>,
    onWalkerClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 360.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = DimensUI.SpaceLarge),
        horizontalArrangement = Arrangement.spacedBy(DimensUI.SpaceMedium),
        verticalArrangement = Arrangement.spacedBy(DimensUI.SpaceMedium)
    ) {
        items(
            items = walkers,
            key = { walker -> walker.userId }
        ) { walker ->
            WalkerCard(
                walker = walker,
                onClick = { onWalkerClick(walker.userId) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WalkerListPreview() {
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
        WalkerList(walkers = walkers, onWalkerClick = {})
    }
}

@Preview(
    name = "Paseadores en ventana amplia",
    showBackground = true,
    widthDp = 840,
    heightDp = 1200
)
@Composable
private fun WalkerListWidePreview() {
    WalkerListPreview()
}
