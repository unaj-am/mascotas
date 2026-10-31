
package com.app.core.data.datasource

import com.app.core.domain.model.Pet
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PetRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val petsCollection
        get() = firestore.collection("pets")

    suspend fun getPetsByOwner(ownerId: String): List<Pet> {
        val snapshot = petsCollection
            .whereEqualTo("ownerId", ownerId)
            .get()
            .await()

        return snapshot.documents.map { document ->
            document.toPet()
        }
    }

    suspend fun getPetById(petId: String): Pet? {
        val document = petsCollection
            .document(petId)
            .get()
            .await()

        if (!document.exists()) {
            return null
        }

        return document.toPet()
    }

    suspend fun savePet(pet: Pet) {
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

    suspend fun deletePet(petId: String) {
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
