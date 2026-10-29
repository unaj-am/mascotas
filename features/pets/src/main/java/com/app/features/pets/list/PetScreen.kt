
package com.app.features.pets.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.core.domain.model.Pet
import com.app.core.ui.components.EmptyContent
import com.app.core.ui.components.ErrorContent
import com.app.core.ui.components.LoadingContent
import com.app.core.ui.components.SectionHeader
import com.app.core.ui.theme.DimensUI
import com.app.core.ui.theme.MascotasThemeUI
import com.app.features.pets.component.PetCard

/**
 * Route de la pantalla de mascotas.
 *
 * Conecta la interfaz con el ViewModel y solicita
 * la carga cuando cambia el identificador del dueño.
 */
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

/**
 * Pantalla visual del listado de mascotas.
 *
 * No realiza consultas directamente: recibe el estado
 * y comunica las acciones al componente superior.
 */
@Composable
fun PetScreen(
    uiState: PetListUiState,
    onPetClick: (String) -> Unit = {},
    onAddPetClick: () -> Unit = {},
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
        SectionHeader(
            title = "Mis mascotas",
            subtitle = when (uiState) {
                is PetListUiState.Success -> {
                    if (uiState.pets.isEmpty()) {
                        "Tus compañeros de aventuras"
                    } else {
                        "Tenés ${uiState.pets.size} mascotas registradas."
                    }
                }

                PetListUiState.Loading ->
                    "Cargando tus mascotas..."

                is PetListUiState.Error ->
                    "No pudimos cargar tus mascotas."
            },
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

        Box(
            modifier = Modifier.weight(1f)
        ) {
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
                            message = "Registrá tu primera mascota para consultar sus datos desde acá.",
                            actionText = "Agregar mi primera mascota",
                            onAction = onAddPetClick,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(
                                DimensUI.SpaceMedium
                            )
                        ) {
                            items(
                                items = uiState.pets,
                                key = { pet -> pet.id }
                            ) { pet ->
                                PetCard(
                                    pet = pet,
                                    onClick = {
                                        onPetClick(pet.id)
                                    }
                                )
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
        )
    )

    MascotasThemeUI {
        PetScreen(
            uiState = PetListUiState.Success(
                pets = mascotas
            )
        )
    }
}
