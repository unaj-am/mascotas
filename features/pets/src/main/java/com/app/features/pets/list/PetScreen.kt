package com.app.features.pets.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.core.domain.model.Pet

@Composable
fun PetRoute(
    ownerId: String,
    viewModel: PetViewModel
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(ownerId) {
        viewModel.loadPets(ownerId)
    }

    PetScreen(uiState = uiState.value)
}

@Composable
fun PetScreen(
    uiState: PetListUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(text = "Mis mascotas")

        when (uiState) {

            is PetListUiState.Loading -> {
                CircularProgressIndicator()
            }

            is PetListUiState.Success -> {

                if (uiState.pets.isEmpty()) {
                    Text(
                        text = "Todavía no tenés mascotas registradas"
                    )
                } else {
                    uiState.pets.forEach { pet ->
                        Text(text = pet.name)
                    }
                }
            }

            is PetListUiState.Error -> {
                Text(text = uiState.message)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PetScreenPreview() {

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

    PetScreen(
        uiState = PetListUiState.Success(
            pets = mascotas
        )
    )
}