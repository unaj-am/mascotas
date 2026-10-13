package com.app.features.pets.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.core.domain.model.Pet

@Composable
fun PetDetailRoute(
    petId: String,
    viewModel: PetDetailViewModel
) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(petId) {
        viewModel.loadPet(petId)
    }

    PetDetailScreen(
        uiState = uiState.value
    )
}

@Composable
fun PetDetailScreen(
    uiState: PetDetailUiState
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Detalle de mascota",
            style = MaterialTheme.typography.headlineMedium
        )

        when (uiState) {

            is PetDetailUiState.Loading -> {
                CircularProgressIndicator()
            }

            is PetDetailUiState.Success -> {

                val pet = uiState.pet

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        Text(
                            text = pet.name,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text(
                            text = "Raza: ${pet.breed}"
                        )

                        Text(
                            text = "Edad: ${pet.age} años"
                        )

                        Text(
                            text = "Peso: ${pet.weight} kg"
                        )

                        Text(
                            text = "Observaciones: ${
                                pet.observations.ifBlank {
                                    "Sin observaciones"
                                }
                            }"
                        )
                    }
                }
            }

            is PetDetailUiState.Error -> {

                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PetDetailScreenPreview() {

    val mascota = Pet(
        id = "PET001",
        ownerId = "USR001",
        name = "Firulais",
        breed = "Labrador",
        age = 3,
        weight = 25.0,
        observations = "Es sociable y tranquilo"
    )

    PetDetailScreen(
        uiState = PetDetailUiState.Success(
            pet = mascota
        )
    )
}