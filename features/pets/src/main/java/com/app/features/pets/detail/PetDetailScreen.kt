package com.app.features.pets.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.core.domain.model.Pet
import com.app.core.ui.components.ErrorContent
import com.app.core.ui.components.LoadingContent
import com.app.core.ui.theme.DimensUI
import com.app.core.ui.theme.MascotasThemeUI

@Composable
fun PetDetailRoute(
    petId: String,
    viewModel: PetDetailViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(petId) {
        viewModel.loadPet(petId)
    }

    PetDetailScreen(
        uiState = uiState,
        onRetry = {
            viewModel.loadPet(petId)
        }
    )
}

@Composable
fun PetDetailScreen(
    uiState: PetDetailUiState,
    onRetry: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(
                horizontal = DimensUI.ScreenHorizontalPadding,
                vertical = DimensUI.ScreenVerticalPadding
            ),
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SectionSpacing
        )
    ) {
        Text(
            text = "Detalle de mascota",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Box(
            modifier = Modifier.weight(1f)
        ) {
            when (uiState) {
                PetDetailUiState.Loading -> {
                    LoadingContent()
                }

                is PetDetailUiState.Success -> {
                    PetDetailContent(
                        pet = uiState.pet
                    )
                }

                is PetDetailUiState.Error -> {
                    ErrorContent(
                        message = uiState.message,
                        onRetry = onRetry
                    )
                }
            }
        }
    }
}

@Composable
private fun PetDetailContent(
    pet: Pet
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(
                    DimensUI.SpaceLarge
                ),
                verticalArrangement = Arrangement.spacedBy(
                    DimensUI.SpaceMedium
                )
            ) {
                Text(
                    text = pet.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                PetDetailField(
                    label = "Raza",
                    value = pet.breed.ifBlank {
                        "No especificada"
                    }
                )

                PetDetailField(
                    label = "Edad",
                    value = "${pet.age} años"
                )

                PetDetailField(
                    label = "Peso",
                    value = "${pet.weight} kg"
                )

                PetDetailField(
                    label = "Tamaño",
                    value = pet.size.ifBlank {
                        "No especificado"
                    }
                )

                PetDetailField(
                    label = "Comportamiento",
                    value = pet.behavior.ifBlank {
                        "No especificado"
                    }
                )

                PetDetailField(
                    label = "Necesidades especiales",
                    value = pet.specialNeeds.ifBlank {
                        "Ninguna registrada"
                    }
                )

                PetDetailField(
                    label = "Observaciones",
                    value = pet.observations.ifBlank {
                        "Sin observaciones"
                    }
                )
            }
        }
    }
}

@Composable
private fun PetDetailField(
    label: String,
    value: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SpaceXSmall
        )
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PetDetailScreenPreview() {
    val mascota = Pet(
        id = "PET001",
        ownerId = "USR001",
        name = "Firulais",
        breed = "Labrador",
        age = 3,
        weight = 25.0,
        observations = "Es sociable y tranquilo"
    )

    MascotasThemeUI {
        PetDetailScreen(
            uiState = PetDetailUiState.Success(
                pet = mascota
            )
        )
    }
}