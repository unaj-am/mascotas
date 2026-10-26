
package com.app.features.walkers.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.app.core.ui.components.AppTextField
import com.app.core.ui.theme.DimensUI

@Composable
fun WalkerProfessionalInfoSection(
    description: String,
    experienceYears: String,
    serviceArea: String,
    pricePerWalk: String,
    maxPets: String,
    isSaving: Boolean,
    onDescriptionChange: (String) -> Unit,
    onExperienceYearsChange: (String) -> Unit,
    onServiceAreaChange: (String) -> Unit,
    onPricePerWalkChange: (String) -> Unit,
    onMaxPetsChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SpaceMedium
        )
    ) {
        Text(
            text = "Perfil profesional",
            style = MaterialTheme.typography.titleLarge
        )

        AppTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = "Descripción",
            placeholder = "Contá tu experiencia y cómo trabajás con mascotas",
            singleLine = false,
            enabled = !isSaving
        )

        AppTextField(
            value = experienceYears,
            onValueChange = onExperienceYearsChange,
            label = "Años de experiencia",
            keyboardType = KeyboardType.Number,
            enabled = !isSaving
        )

        AppTextField(
            value = serviceArea,
            onValueChange = onServiceAreaChange,
            label = "Zona de servicio",
            enabled = !isSaving
        )

        AppTextField(
            value = pricePerWalk,
            onValueChange = onPricePerWalkChange,
            label = "Precio por paseo",
            keyboardType = KeyboardType.Decimal,
            enabled = !isSaving
        )

        AppTextField(
            value = maxPets,
            onValueChange = onMaxPetsChange,
            label = "Máximo de mascotas por paseo",
            keyboardType = KeyboardType.Number,
            enabled = !isSaving
        )
    }
}
