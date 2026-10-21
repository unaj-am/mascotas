package com.app.features.pets.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.app.core.domain.model.Pet
import com.app.core.ui.components.AppTextField
import com.app.core.ui.components.DomainErrorMessage
import com.app.core.ui.components.PrimaryActionButton
import com.app.core.ui.theme.DimensUI
import java.util.UUID

@Composable
fun PetFormScreen(
    ownerId: String,
    pet: Pet? = null,
    isSaving: Boolean = false,
    errorMessage: String? = null,
    onSave: (Pet) -> Unit,
    onCancel: () -> Unit
) {
    var name by rememberSaveable(pet?.id) {
        mutableStateOf(pet?.name.orEmpty())
    }

    var breed by rememberSaveable(pet?.id) {
        mutableStateOf(pet?.breed.orEmpty())
    }

    var age by rememberSaveable(pet?.id) {
        mutableStateOf(pet?.age?.toString().orEmpty())
    }

    var weight by rememberSaveable(pet?.id) {
        mutableStateOf(pet?.weight?.toString().orEmpty())
    }

    var size by rememberSaveable(pet?.id) {
        mutableStateOf(pet?.size.orEmpty())
    }

    var behavior by rememberSaveable(pet?.id) {
        mutableStateOf(pet?.behavior.orEmpty())
    }

    var specialNeeds by rememberSaveable(pet?.id) {
        mutableStateOf(pet?.specialNeeds.orEmpty())
    }

    var observations by rememberSaveable(pet?.id) {
        mutableStateOf(pet?.observations.orEmpty())
    }

    var validationError by rememberSaveable(pet?.id) {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            validationError = errorMessage
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = DimensUI.ScreenHorizontalPadding,
                vertical = DimensUI.ScreenVerticalPadding
            ),
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SpaceMedium
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (pet == null) {
                "Registrar mascota"
            } else {
                "Editar mascota"
            },
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        AppTextField(
            value = name,
            onValueChange = {
                name = it
                validationError = null
            },
            label = "Nombre",
            enabled = !isSaving
        )

        AppTextField(
            value = breed,
            onValueChange = {
                breed = it
                validationError = null
            },
            label = "Raza",
            enabled = !isSaving
        )

        AppTextField(
            value = age,
            onValueChange = {
                age = it
                validationError = null
            },
            label = "Edad en años",
            keyboardType = KeyboardType.Number,
            enabled = !isSaving
        )

        AppTextField(
            value = weight,
            onValueChange = {
                weight = it
                validationError = null
            },
            label = "Peso en kg",
            keyboardType = KeyboardType.Decimal,
            enabled = !isSaving
        )

        AppTextField(
            value = size,
            onValueChange = {
                size = it
                validationError = null
            },
            label = "Tamaño",
            enabled = !isSaving
        )

        AppTextField(
            value = behavior,
            onValueChange = {
                behavior = it
                validationError = null
            },
            label = "Comportamiento",
            singleLine = false,
            enabled = !isSaving
        )

        AppTextField(
            value = specialNeeds,
            onValueChange = {
                specialNeeds = it
                validationError = null
            },
            label = "Necesidades especiales",
            singleLine = false,
            enabled = !isSaving
        )

        AppTextField(
            value = observations,
            onValueChange = {
                observations = it
                validationError = null
            },
            label = "Observaciones",
            singleLine = false,
            enabled = !isSaving
        )

        validationError?.let { message ->
            DomainErrorMessage(
                message = message
            )
        }

        PrimaryActionButton(
            text = "Guardar",
            onClick = {
                val parsedAge = age.toIntOrNull()
                val parsedWeight = weight
                    .replace(',', '.')
                    .toDoubleOrNull()

                validationError = when {
                    name.isBlank() ->
                        "Ingresá el nombre de la mascota"

                    parsedAge == null || parsedAge < 0 ->
                        "Ingresá una edad válida, igual o mayor que cero"

                    parsedWeight == null ||
                            !parsedWeight.isFinite() ||
                            parsedWeight <= 0 ->
                        "Ingresá un peso mayor que cero"

                    else -> null
                }

                if (validationError == null) {
                    onSave(
                        Pet(
                            id = pet?.id ?: UUID.randomUUID().toString(),
                            ownerId = ownerId,
                            name = name.trim(),
                            breed = breed.trim(),
                            age = parsedAge!!,
                            weight = parsedWeight!!,
                            observations = observations.trim(),
                            photoUri = pet?.photoUri,
                            size = size.trim(),
                            behavior = behavior.trim(),
                            specialNeeds = specialNeeds.trim()
                        )
                    )
                }
            },
            enabled = !isSaving,
            isLoading = isSaving
        )

        TextButton(
            onClick = onCancel,
            enabled = !isSaving
        ) {
            Text("Cancelar")
        }
    }
}