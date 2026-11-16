package com.app.features.pets.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.core.domain.model.Pet
import com.app.core.ui.components.AppAnimatedVisibility
import com.app.core.ui.components.EmptyContent
import com.app.core.ui.components.ErrorContent
import com.app.core.ui.components.LoadingContent
import com.app.core.ui.components.SectionHeader
import com.app.core.ui.theme.DimensUI
import com.app.core.ui.theme.MascotasThemeUI
import com.app.features.pets.component.PetCard

@Composable
fun PetRoute(
    ownerId: String,
    viewModel: PetViewModel,
    onPetClick: (String) -> Unit = {},
    onAddPetClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(ownerId) {
        viewModel.loadPets(ownerId)
    }

    PetScreen(
        uiState = uiState,
        onPetClick = onPetClick,
        onAddPetClick = onAddPetClick,
        onRetry = { viewModel.loadPets(ownerId) },
        modifier = modifier
    )
}

@Composable
fun PetScreen(
    uiState: PetListUiState,
    onPetClick: (String) -> Unit = {},
    onAddPetClick: () -> Unit = {},
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier
) {
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
            verticalArrangement = Arrangement.spacedBy(DimensUI.SectionSpacing)
        ) {
            SectionHeader(
                title = "Mis mascotas",
                actionText = if (
                    uiState is PetListUiState.Success &&
                    uiState.pets.isNotEmpty()
                ) {
                    "Agregar"
                } else {
                    null
                },
                onAction = onAddPetClick
            )

            Box(modifier = Modifier.weight(1f)) {
                when (uiState) {
                    PetListUiState.Loading -> {
                        LoadingContent(
                            modifier = Modifier.fillMaxSize(),
                            message = "Cargando tus mascotas..."
                        )
                    }

                    is PetListUiState.Success -> {
                        if (uiState.pets.isEmpty()) {
                            EmptyContent(
                                title = "Todavía no tenés mascotas",
                                message = "Registrá a tu compañero para guardar sus datos, características y necesidades especiales.",
                                actionText = "Agregar mi primera mascota",
                                onAction = onAddPetClick,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(minSize = 320.dp),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    bottom = DimensUI.SpaceLarge
                                ),
                                horizontalArrangement = Arrangement.spacedBy(
                                    DimensUI.SpaceMedium
                                ),
                                verticalArrangement = Arrangement.spacedBy(
                                    DimensUI.SpaceMedium
                                )
                            ) {
                                items(
                                    items = uiState.pets,
                                    key = { pet -> pet.id }
                                ) { pet ->
                                    AppAnimatedVisibility(
                                        visible = true,
                                        durationMillis = 240
                                    ) {
                                        PetCard(
                                            pet = pet,
                                            onClick = { onPetClick(pet.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    is PetListUiState.Error -> {
                        ErrorContent(
                            message = uiState.message,
                            onRetry = onRetry,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PetScreenPreview() {
    val mascotas = listOf(
        Pet(
            id = "PET001",
            ownerId = "USR001",
            name = "Firulais",
            breed = "Labrador",
            age = 3,
            weight = 25.0
        ),
        Pet(
            id = "PET002",
            ownerId = "USR001",
            name = "Luna",
            breed = "Mestiza",
            age = 2,
            weight = 12.5
        ),
        Pet(
        id = "PET003",
        ownerId = "USR001",
        name = "Lunita",
        breed = "Chiuaua",
        age = 2,
        weight = 12.5
        ),
        Pet(
            id = "PET04",
            ownerId = "USR001",
            name = "Pepito",
            breed = "Chiuaua",
            age = 2,
            weight = 12.5
        )
    )

    MascotasThemeUI {
        PetScreen(uiState = PetListUiState.Success(mascotas))
    }
}

@Preview(
    name = "Mascotas en pantalla amplia",
    showBackground = true,
    widthDp = 840,
    heightDp = 1200
)
@Composable
private fun PetScreenWidePreview() {
    PetScreenPreview()
}
