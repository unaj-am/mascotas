
package com.app.core.data.repository

import com.app.core.database.dao.PetDao
import com.app.core.database.mapper.toDomain
import com.app.core.database.mapper.toEntity
import com.app.core.domain.model.Pet
import com.app.core.domain.repository.PetRepository
import kotlinx.coroutines.flow.first

class PetRepositoryImpl(
    private val petDao: PetDao
) : PetRepository {

    override suspend fun getPetsByOwner(
        ownerId: String
    ): List<Pet> {
        return petDao.getPetsByOwner(ownerId)
            .first()
            .map { it.toDomain() }
    }

    override suspend fun getPetById(
        petId: String
    ): Pet? {
        return petDao.getPetById(petId)?.toDomain()
    }

    override suspend fun savePet(
        pet: Pet
    ) {
        petDao.insertPet(pet.toEntity())
    }

    override suspend fun deletePet(
        petId: String
    ) {
        petDao.deletePetById(petId)
    }
}
