package com.app.features.pets.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.app.core.domain.model.Pet
import com.app.core.ui.components.AppTextField
import com.app.core.ui.components.DomainErrorMessage
import com.app.core.ui.components.PrimaryActionButton
import com.app.core.ui.theme.DimensUI
import com.app.features.pets.form.component.PetAgeField
import com.app.features.pets.form.component.PetBehaviorSelector
import com.app.features.pets.form.component.PetSizeSelector
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = DimensUI.ScreenHorizontalPadding,
                vertical = DimensUI.ScreenVerticalPadding
            ),
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SpaceMedium
        )
    ) {
        Text(
            text = if (pet == null) {
                "Registrar mascota"
            } else {
                "Editar mascota"
            },
            style = MaterialTheme.typography.headlineMedium
        )

        AppTextField(
            value = name,
            onValueChange = {
                name = it
                validationError = null
            },
            label = "Nombre",
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth()
        )

        AppTextField(
            value = breed,
            onValueChange = {
                breed = it
                validationError = null
            },
            label = "Raza",
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth()
        )

        PetAgeField(
            value = age,
            onValueChange = {
                age = it
                validationError = null
            },
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
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth()
        )

        PetSizeSelector(
            value = size,
            onValueChange = {
                size = it
                validationError = null
            },
            enabled = !isSaving
        )

        PetBehaviorSelector(
            value = behavior,
            onValueChange = {
                behavior = it
                validationError = null
            },
            enabled = !isSaving
        )

        AppTextField(
            value = specialNeeds,
            onValueChange = {
                specialNeeds = it
                validationError = null
            },
            label = "Necesidades especiales",
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth()
        )

        AppTextField(
            value = observations,
            onValueChange = {
                observations = it
                validationError = null
            },
            label = "Observaciones",
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth()
        )

        (validationError ?: errorMessage)?.let { message ->
            DomainErrorMessage(message = message)
        }

        PrimaryActionButton(
            text = if (isSaving) {
                "Guardando..."
            } else if (pet == null) {
                "Registrar mascota"
            } else {
                "Guardar cambios"
            },
            onClick = {
                val parsedAge = age.toIntOrNull()
                val parsedWeight = weight
                    .trim()
                    .replace(',', '.')
                    .toDoubleOrNull()

                validationError = when {
                    name.isBlank() ->
                        "Ingresá el nombre de la mascota."

                    parsedAge == null || parsedAge < 0 ->
                        "Ingresá una edad válida."

                    parsedWeight == null ||
                            !parsedWeight.isFinite() ||
                            parsedWeight <= 0.0 ->
                        "Ingresá un peso válido mayor que cero."

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
                            size = size,
                            behavior = behavior,
                            specialNeeds = specialNeeds.trim()
                        )
                    )
                }
            },
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth()
        )
            TextButton(
                onClick = onCancel,
                enabled = !isSaving
            ) {
                Text("Cancelar")
            }
        }
    }
