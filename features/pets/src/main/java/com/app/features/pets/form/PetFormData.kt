package com.app.features.pets.form

import com.app.core.domain.model.Pet
import java.util.UUID

/**
 * Presentation input for creating or editing a pet.
 *
 * Keeps text-field parsing and validation out of the Compose screen while preserving
 * the form's existing validation rules.
 */
data class PetFormData(
    val name: String,
    val breed: String,
    val age: String,
    val weight: String,
    val size: String,
    val behavior: String,
    val specialNeeds: String,
    val observations: String
) {
    fun toPet(ownerId: String, existingPet: Pet? = null): Result<Pet> {
        val parsedAge = age.toIntOrNull()
        val parsedWeight = weight.trim().replace(',', '.').toDoubleOrNull()

        val validationError = when {
            name.isBlank() -> "Ingresá el nombre de la mascota."
            parsedAge == null || parsedAge < 0 -> "Ingresá una edad válida."
            parsedWeight == null || !parsedWeight.isFinite() || parsedWeight <= 0.0 ->
                "Ingresá un peso válido mayor que cero."
            else -> null
        }

        if (validationError != null) return Result.failure(InvalidPetFormException(validationError))

        return Result.success(
            Pet(
                id = existingPet?.id ?: UUID.randomUUID().toString(),
                ownerId = ownerId,
                name = name.trim(),
                breed = breed.trim(),
                age = requireNotNull(parsedAge),
                weight = requireNotNull(parsedWeight),
                observations = observations.trim(),
                photoUri = existingPet?.photoUri,
                size = size,
                behavior = behavior,
                specialNeeds = specialNeeds.trim()
            )
        )
    }
}

class InvalidPetFormException(message: String) : IllegalArgumentException(message)
