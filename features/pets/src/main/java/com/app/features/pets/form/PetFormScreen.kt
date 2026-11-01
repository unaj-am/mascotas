
package com.app.features.pets.form

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.app.core.domain.model.Pet
import com.app.core.ui.components.AppCard
import com.app.core.ui.components.AppTextField
import com.app.core.ui.components.DomainErrorMessage
import com.app.core.ui.components.PrimaryActionButton
import com.app.core.ui.components.ProfilePhotoPicker
import com.app.core.ui.components.SectionHeader
import com.app.core.ui.theme.DimensUI
import com.app.features.pets.detail.component.PetPhoto
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
    onSave: (Pet, Uri?) -> Unit,
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

    var selectedPhotoUri by remember(pet?.id) {
        mutableStateOf<Uri?>(null)
    }

    val isEditing = pet != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = DimensUI.ScreenHorizontalPadding,
                vertical = DimensUI.ScreenVerticalPadding
            ),
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SectionSpacing
        )
    ) {

        // 1. ENCABEZADO
        SectionHeader(
            title = if (isEditing) {
                "Editar mascota"
            } else {
                "Registrar mascota"
            },
            subtitle = if (isEditing) {
                "Actualizá la información de ${pet?.name}."
            } else {
                "Completá los datos para registrar a tu compañero."
            }
        )

        Text(
            text = "* Campos obligatorios",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // 2. FOTOGRAFÍA
        AppCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    DimensUI.SpaceMedium
                )
            ) {
                Text(
                    text = "Fotografía",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Agregá una imagen para identificar fácilmente a tu mascota.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                PetPhoto(
                    photoUri = selectedPhotoUri?.toString()
                        ?: pet?.photoUri,
                    modifier = Modifier.padding(
                        vertical = DimensUI.SpaceSmall
                    )
                )

                ProfilePhotoPicker(
                    onPhotoSelected = { uri ->
                        selectedPhotoUri = uri
                        validationError = null
                    }
                ) { openPicker ->
                    TextButton(
                        onClick = openPicker,
                        enabled = !isSaving,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (
                                selectedPhotoUri != null ||
                                !pet?.photoUri.isNullOrBlank()
                            ) {
                                "Cambiar fotografía"
                            } else {
                                "Seleccionar fotografía"
                            }
                        )
                    }
                }
            }
        }

        // 3. DATOS PRINCIPALES
        AppCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    DimensUI.SpaceLarge
                )
            ) {
                Text(
                    text = "Datos principales",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Información básica de tu mascota.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                AppTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        validationError = null
                    },
                    label = "Nombre *",
                    placeholder = "Ej. Firulais",
                    enabled = !isSaving
                )

                AppTextField(
                    value = breed,
                    onValueChange = {
                        breed = it
                        validationError = null
                    },
                    label = "Raza",
                    placeholder = "Ej. Labrador",
                    enabled = !isSaving
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
                    label = "Peso en kg *",
                    placeholder = "Ej. 12,5",
                    keyboardType = KeyboardType.Decimal,
                    enabled = !isSaving
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
            }
        }

        // 4. INFORMACIÓN ADICIONAL
        AppCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    DimensUI.SpaceLarge
                )
            ) {
                Text(
                    text = "Información adicional",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Estos datos ayudan a conocer mejor las necesidades de tu mascota.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                AppTextField(
                    value = specialNeeds,
                    onValueChange = {
                        specialNeeds = it
                        validationError = null
                    },
                    label = "Necesidades especiales",
                    placeholder = "Alergias, cuidados u otras necesidades",
                    enabled = !isSaving,
                    singleLine = false
                )

                AppTextField(
                    value = observations,
                    onValueChange = {
                        observations = it
                        validationError = null
                    },
                    label = "Observaciones",
                    placeholder = "Agregá información útil para el paseador",
                    enabled = !isSaving,
                    singleLine = false
                )
            }
        }

        // 5. ERRORES
        (validationError ?: errorMessage)?.let { message ->
            DomainErrorMessage(
                message = message,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 6. ACCIONES
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(
                DimensUI.SpaceSmall
            )
        ) {
            PrimaryActionButton(
                text = if (isEditing) {
                    "Guardar cambios"
                } else {
                    "Registrar mascota"
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
                                id = pet?.id
                                    ?: UUID.randomUUID().toString(),
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
                            ),
                            selectedPhotoUri
                        )
                    }
                },
                enabled = !isSaving,
                isLoading = isSaving
            )

            TextButton(
                onClick = onCancel,
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }
        }
    }
}
