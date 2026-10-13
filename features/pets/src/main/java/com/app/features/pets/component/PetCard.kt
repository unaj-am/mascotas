package com.app.features.pets.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.core.domain.model.Pet

@Composable
fun PetCard(
    pet: Pet,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = pet.name,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Raza: ${pet.breed}"
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = "Edad: ${pet.age} años"
                )

                Text(
                    text = "Peso: ${pet.weight} kg"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PetCardPreview() {

    val mascota = Pet(
        id = "PET001",
        ownerId = "USR001",
        name = "Firulais",
        breed = "Labrador",
        age = 3,
        weight = 25.0
    )

    PetCard(
        pet = mascota,
        modifier = Modifier.padding(16.dp)
    )
}