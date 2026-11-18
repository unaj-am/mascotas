
package com.app.core.data.repository

import com.app.core.data.mapper.toFirestoreMap
import com.app.core.data.mapper.toPet
import com.app.core.database.dao.PetDao
import com.app.core.database.mapper.toDomain
import com.app.core.database.mapper.toEntity
import com.app.core.domain.model.Pet
import com.app.core.domain.repository.PetRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PetRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val petDao: PetDao
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

        return try {

            // 1. Consultar Firestore directamente al servidor.
            val snapshot = petsCollection
                .whereEqualTo("ownerId", ownerId)
                .get(Source.SERVER)
                .await()

            // 2. Convertir documentos a modelos de dominio.
            val pets = snapshot.documents.map { document ->
                document.toPet()
            }

            // 3. Actualizar la caché local.
            petDao.replacePetsByOwner(
                ownerId = ownerId,
                pets = pets.map { it.toEntity() }
            )

            // 4. Devolver información actualizada.
            pets

        } catch (exception: CancellationException) {
            throw exception

        } catch (exception: FirebaseFirestoreException) {

            if (!exception.isConnectivityError()) {
                throw exception
            }

            // Sin conexión: recuperar mascotas desde Room.
            getLocalPets(ownerId)
        }
    }

    override suspend fun getPetById(
        petId: String
    ): Pet? {

        val currentUserId = requireCurrentUserId()

        require(petId.isNotBlank()) {
            "El identificador de la mascota no puede estar vacío"
        }

        return try {

            // 1. Consultar Firestore.
            val document = petsCollection
                .document(petId)
                .get(Source.SERVER)
                .await()

            // 2. Si no existe, no devolver un registro obsoleto.
            if (!document.exists()) {
                petDao.getPetById(petId)
                    ?.takeIf { it.ownerId == currentUserId }
                    ?.let { petDao.deletePetById(petId) }

                return null
            }

            // 3. Convertir y validar propietario.
            val pet = document.toPet()

            require(pet.ownerId == currentUserId) {
                "No podés consultar mascotas de otro usuario"
            }

            // 4. Actualizar caché.
            petDao.insertPet(pet.toEntity())

            pet

        } catch (exception: CancellationException) {
            throw exception

        } catch (exception: FirebaseFirestoreException) {

            if (!exception.isConnectivityError()) {
                throw exception
            }

            // Sin conexión: consultar Room.
            getLocalPet(
                petId = petId,
                ownerId = currentUserId
            )
        }
    }

    override suspend fun savePet(
        pet: Pet
    ) {

        val currentUserId = requireCurrentUserId()

        require(pet.ownerId == currentUserId) {
            "No podés guardar mascotas de otro usuario"
        }

        val petData = pet.toFirestoreMap()

        // 1. Guardar en Firestore.
        petsCollection
            .document(pet.id)
            .set(petData)
            .await()

        // 2. Actualizar Room después de confirmar el servidor.
        petDao.insertPet(pet.toEntity())
    }

    override suspend fun deletePet(
        petId: String
    ) {

        val currentUserId = requireCurrentUserId()

        // 1. Recuperar mascota y validar propietario.
        val pet = getPetById(petId)
            ?: throw IllegalArgumentException(
                "No existe la mascota que querés eliminar"
            )

        require(pet.ownerId == currentUserId) {
            "No podés eliminar mascotas de otro usuario"
        }

        // 2. Eliminar de Firestore.
        petsCollection
            .document(petId)
            .delete()
            .await()

        // 3. Eliminar de Room.
        petDao.deletePetById(petId)
    }

    private suspend fun getLocalPets(
        ownerId: String
    ): List<Pet> {

        return petDao
            .getPetsByOwner(ownerId)
            .first()
            .map { it.toDomain() }
    }

    private suspend fun getLocalPet(
        petId: String,
        ownerId: String
    ): Pet? {

        return petDao
            .getPetById(petId)
            ?.takeIf { it.ownerId == ownerId }
            ?.toDomain()
    }

    private fun FirebaseFirestoreException.isConnectivityError(): Boolean {

        return code == FirebaseFirestoreException.Code.UNAVAILABLE ||
                code == FirebaseFirestoreException.Code.DEADLINE_EXCEEDED
    }

}
