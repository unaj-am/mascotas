package com.app.features.pets.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
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
import com.app.features.pets.detail.component.PetDetailContent

@Composable
fun PetDetailRoute(
    petId: String,
    viewModel: PetDetailViewModel,
    onEditClick: (Pet) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(petId) {
        viewModel.loadPet(petId)
    }

    PetDetailScreen(
        uiState = uiState,
        onRetry = { viewModel.loadPet(petId) },
        onEditClick = onEditClick
    )
}

@Composable
fun PetDetailScreen(
    uiState: PetDetailUiState,
    onRetry: () -> Unit = {},
    onEditClick: (Pet) -> Unit = {}
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
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(
                            DimensUI.SpaceMedium
                        )
                    ) {
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            PetDetailContent(
                                pet = uiState.pet
                            )
                        }

                        Button(
                            onClick = {
                                onEditClick(uiState.pet)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Editar mascota")
                        }
                    }
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

@Preview(showBackground = true)
@Composable
private fun PetDetailScreenPreview() {
    val pet = Pet(
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
            uiState = PetDetailUiState.Success(pet)
        )
    }
}