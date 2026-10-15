
package com.app.features.pets.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.core.domain.model.Pet

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
    var observations by rememberSaveable(pet?.id) {
        mutableStateOf(pet?.observations.orEmpty())
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

    var validationError by remember {
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
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (pet == null) {
                "Registrar mascota"
            } else {
                "Editar mascota"
            }
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isSaving
        )

        OutlinedTextField(
            value = breed,
            onValueChange = { breed = it },
            label = { Text("Raza") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isSaving
        )

        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Edad en años") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isSaving
        )

        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Peso en kg") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isSaving
        )

        OutlinedTextField(
            value = size,
            onValueChange = { size = it },
            label = { Text("Tamaño") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isSaving
        )

        OutlinedTextField(
            value = behavior,
            onValueChange = { behavior = it },
            label = { Text("Comportamiento") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving
        )

        OutlinedTextField(
            value = specialNeeds,
            onValueChange = { specialNeeds = it },
            label = { Text("Necesidades especiales") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving
        )

        OutlinedTextField(
            value = observations,
            onValueChange = { observations = it },
            label = { Text("Observaciones") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving
        )

        if (validationError != null) {
            Text(
                text = validationError.orEmpty()
            )
        }

        Button(
            onClick = {
                val parsedAge = age.toIntOrNull()
                val parsedWeight = weight.replace(',', '.').toDoubleOrNull()

                validationError = when {
                    name.isBlank() ->
                        "Ingresá el nombre de la mascota"

                    parsedAge == null || parsedAge < 0 ->
                        "Ingresá una edad válida, igual o mayor que cero"

                    parsedWeight == null || parsedWeight <= 0 ->
                        "Ingresá un peso mayor que cero"

                    else -> null
                }

                if (validationError == null) {
                    onSave(
                        Pet(
                            id = pet?.id ?: java.util.UUID.randomUUID().toString(),
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
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving
        ) {
            Text(
                text = if (isSaving) {
                    "Guardando..."
                } else {
                    "Guardar"
                }
            )
        }

        TextButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving
        ) {
            Text("Cancelar")
        }
    }
}
