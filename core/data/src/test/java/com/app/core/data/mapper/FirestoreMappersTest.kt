package com.app.core.data.mapper

import com.app.core.domain.model.Pet
import com.app.core.domain.model.RequestStatus
import com.app.core.domain.model.User
import com.app.core.domain.model.UserRole
import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkLocation
import com.app.core.domain.model.WalkRequest
import com.app.core.domain.model.WalkStatus
import com.app.core.domain.model.Walker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class FirestoreMappersTest {

    @Test
    fun petToFirestoreMapContainsPetFieldsAndNullablePhoto() {
        val pet = Pet(
            id = "pet-1",
            ownerId = "owner-1",
            name = "Luna",
            breed = "Mestiza",
            age = 4,
            weight = 12.5,
            observations = "Sociable",
            photoUri = null,
            size = "MEDIUM",
            behavior = "CALM",
            specialNeeds = ""
        )

        assertEquals(
            mapOf(
                "ownerId" to "owner-1",
                "name" to "Luna",
                "breed" to "Mestiza",
                "age" to 4,
                "weight" to 12.5,
                "observations" to "Sociable",
                "photoUri" to null,
                "size" to "MEDIUM",
                "behavior" to "CALM",
                "specialNeeds" to ""
            ),
            pet.toFirestoreMap()
        )
    }

    @Test
    fun userToFirestoreMapPersistsRoleNameAndActiveState() {
        val user = User(
            id = "user-1",
            name = "Ana",
            lastName = "Pérez",
            email = "ana@example.com",
            phone = "123",
            profileImage = "https://example.com/photo.jpg",
            role = UserRole.OWNER,
            active = false
        )

        assertEquals(
            mapOf(
                "name" to "Ana",
                "lastName" to "Pérez",
                "email" to "ana@example.com",
                "phone" to "123",
                "profileImage" to "https://example.com/photo.jpg",
                "role" to "OWNER",
                "active" to false
            ),
            user.toFirestoreMap()
        )
    }

    @Test
    fun walkerUpdateMapOmitsRatingWhileFullMapIncludesIt() {
        val walker = Walker(
            userId = "walker-1",
            description = "Cuida perros",
            experienceYears = 5,
            serviceArea = "Centro",
            available = true,
            pricePerWalk = 2500.0,
            maxPets = 2,
            rating = 4.8
        )

        assertEquals(4.8, walker.toFirestoreMap()["rating"])
        assertEquals(
            setOf(
                "description",
                "experienceYears",
                "serviceArea",
                "available",
                "pricePerWalk",
                "maxPets"
            ),
            walker.toFirestoreUpdateMap().keys
        )
    }

    @Test
    fun walkMapsPreserveExistingOptionalFieldFormats() {
        val walk = Walk(
            id = "walk-1",
            ownerId = "owner-1",
            walkerId = "walker-1",
            petIds = listOf("pet-1"),
            scheduledAt = "2026-11-14T12:00:00Z",
            durationMinutes = 30,
            status = WalkStatus.IN_PROGRESS
        )

        val nullableMap = walk.toFirestoreMap()
        val defaultedMap = walk.toFirestoreMapWithDefaults()

        assertEquals(WalkStatus.IN_PROGRESS.name, nullableMap["status"])
        assertNull(nullableMap["startedAt"])
        assertNull(nullableMap["finishedAt"])
        assertNull(nullableMap["distanceKm"])
        assertEquals("", defaultedMap["startedAt"])
        assertEquals("", defaultedMap["finishedAt"])
        assertEquals(0.0, defaultedMap["distanceKm"])
    }

    @Test
    fun walkRequestToFirestoreMapAddsDisplayDataAndServerTimestamp() {
        val request = WalkRequest(
            id = "request-1",
            ownerId = "owner-1",
            walkerId = "walker-1",
            petIds = listOf("pet-1"),
            scheduledAt = "2026-11-14T12:00:00Z",
            durationMinutes = 45,
            status = RequestStatus.PENDING,
            observations = "Llamar al llegar",
            petPhotos = listOf("https://example.com/pet.jpg")
        )

        val map = request.toFirestoreMap(
            ownerName = "Ana Pérez",
            petNames = listOf("Luna")
        )

        assertEquals("Ana Pérez", map["ownerName"])
        assertEquals(listOf("Luna"), map["petNames"])
        assertEquals(request.petPhotos, map["petPhotos"])
        assertEquals(RequestStatus.PENDING.name, map["status"])
        assertNotNull(map["createdAt"])
    }

    @Test
    fun walkLocationToFirestoreMapConvertsFloatMeasurementsAndKeepsNulls() {
        val location = WalkLocation(
            latitude = -34.6,
            longitude = -58.4,
            accuracy = 8.5f,
            bearing = null,
            speed = null,
            recordedAt = 1_800_000_000_000L
        )

        val map = location.toFirestoreMap()

        assertEquals(-34.6, map["latitude"])
        assertEquals(-58.4, map["longitude"])
        assertEquals(8.5, map["accuracy"])
        assertNull(map["bearing"])
        assertNull(map["speed"])
        assertEquals(1_800_000_000_000L, map["recordedAt"])
    }
}
