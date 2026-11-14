package com.app.features.pets.form

import com.app.core.domain.model.Pet
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PetFormDataTest {

    @Test
    fun toPet_trimsFieldsAndParsesDecimalComma() {
        val result = validForm(name = "  Firulais ", weight = "12,5")
            .toPet(ownerId = "owner-1")

        val pet = result.getOrThrow()
        assertEquals("Firulais", pet.name)
        assertEquals("owner-1", pet.ownerId)
        assertEquals(12.5, pet.weight, 0.0)
        assertEquals("Labrador", pet.breed)
        assertEquals(3, pet.age)
    }

    @Test
    fun toPet_preservesIdAndPhotoWhenEditing() {
        val existing = Pet(
            id = "pet-1",
            ownerId = "owner-1",
            name = "Old name",
            breed = "",
            age = 1,
            weight = 2.0,
            photoUri = "https://example.com/pet.jpg"
        )

        val pet = validForm(name = "New name").toPet("owner-1", existing).getOrThrow()

        assertEquals("pet-1", pet.id)
        assertEquals(existing.photoUri, pet.photoUri)
        assertEquals("New name", pet.name)
    }

    @Test
    fun toPet_rejectsBlankName() {
        val result = validForm(name = " ").toPet("owner-1")

        assertTrue(result.exceptionOrNull() is InvalidPetFormException)
        assertEquals("Ingresá el nombre de la mascota.", result.exceptionOrNull()?.message)
    }

    @Test
    fun toPet_rejectsNegativeAge() {
        val result = validForm(age = "-1").toPet("owner-1")

        assertEquals("Ingresá una edad válida.", result.exceptionOrNull()?.message)
    }

    @Test
    fun toPet_rejectsNonPositiveOrNonFiniteWeight() {
        listOf("0", "-1", "NaN", "Infinity", "invalid").forEach { weight ->
            val result = validForm(weight = weight).toPet("owner-1")
            assertEquals(
                "Ingresá un peso válido mayor que cero.",
                result.exceptionOrNull()?.message
            )
        }
    }

    private fun validForm(
        name: String = "Luna",
        age: String = "3",
        weight: String = "8.2"
    ) = PetFormData(
        name = name,
        breed = "Labrador",
        age = age,
        weight = weight,
        size = "Mediano",
        behavior = "Tranquilo",
        specialNeeds = "Sin restricciones",
        observations = "Sociable"
    )
}
