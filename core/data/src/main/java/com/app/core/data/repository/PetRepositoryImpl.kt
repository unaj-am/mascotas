package com.app.core.data.repository

import com.app.core.domain.model.Pet
import com.app.core.domain.repository.PetRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PetRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : PetRepository {

    private val petsCollection
        get() = firestore.collection("pets")

    private fun requireCurrentUserId(): String {
        return firebaseAuth.currentUser?.uid
            ?: throw IllegalStateException(
                "Debés iniciar sesión para gestionar mascotas"
            )
    }

    override suspend fun getPetsByOwner(
        ownerId: String
    ): List<Pet> {
        val currentUserId = requireCurrentUserId()

        require(ownerId == currentUserId) {
            "No podés consultar mascotas de otro usuario"
        }

        val snapshot = petsCollection
            .whereEqualTo("ownerId", ownerId)
            .get()
            .await()

        return snapshot.documents.map { document ->
            document.toPet()
        }
    }

    override suspend fun getPetById(
        petId: String
    ): Pet? {
        val currentUserId = requireCurrentUserId()

        val document = petsCollection
            .document(petId)
            .get()
            .await()

        if (!document.exists()) {
            return null
        }

        val pet = document.toPet()

        require(pet.ownerId == currentUserId) {
            "No podés consultar una mascota de otro usuario"
        }

        return pet
    }

    override suspend fun savePet(
        pet: Pet
    ) {
        val currentUserId = requireCurrentUserId()

        require(pet.ownerId == currentUserId) {
            "No podés guardar mascotas de otro usuario"
        }

        val petData = mapOf(
            "ownerId" to pet.ownerId,
            "name" to pet.name,
            "breed" to pet.breed,
            "age" to pet.age,
            "weight" to pet.weight,
            "observations" to pet.observations,
            "photoUri" to pet.photoUri,
            "size" to pet.size,
            "behavior" to pet.behavior,
            "specialNeeds" to pet.specialNeeds
        )

        petsCollection
            .document(pet.id)
            .set(petData)
            .await()
    }

    override suspend fun deletePet(
        petId: String
    ) {
        val pet = getPetById(petId)
            ?: throw IllegalArgumentException(
                "No existe la mascota que querés eliminar"
            )

        require(pet.ownerId == requireCurrentUserId()) {
            "No podés eliminar mascotas de otro usuario"
        }

        petsCollection
            .document(petId)
            .delete()
            .await()
    }

    private fun DocumentSnapshot.toPet(): Pet {
        return Pet(
            id = id,
            ownerId = getString("ownerId")
                ?: throw IllegalStateException(
                    "La mascota no tiene dueño"
                ),
            name = getString("name").orEmpty(),
            breed = getString("breed").orEmpty(),
            age = getLong("age")?.toInt() ?: 0,
            weight = getDouble("weight") ?: 0.0,
            observations = getString("observations").orEmpty(),
            photoUri = getString("photoUri"),
            size = getString("size").orEmpty(),
            behavior = getString("behavior").orEmpty(),
            specialNeeds = getString("specialNeeds").orEmpty()
        )
    }
}