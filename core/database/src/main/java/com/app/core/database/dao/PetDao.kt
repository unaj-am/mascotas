
package com.app.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.app.core.database.model.PetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {

    @Query("""
        SELECT * FROM pets
        WHERE ownerId = :ownerId
        ORDER BY name ASC
    """)
    fun getPetsByOwner(
        ownerId: String
    ): Flow<List<PetEntity>>

    @Query("""
        SELECT * FROM pets
        WHERE id = :petId
        LIMIT 1
    """)
    suspend fun getPetById(
        petId: String
    ): PetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPet(
        pet: PetEntity
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPets(
        pets: List<PetEntity>
    )

    @Update
    suspend fun updatePet(
        pet: PetEntity
    )

    @Delete
    suspend fun deletePet(
        pet: PetEntity
    )

    @Query("DELETE FROM pets WHERE id = :petId")
    suspend fun deletePetById(
        petId: String
    )

    @Query("DELETE FROM pets WHERE ownerId = :ownerId")
    suspend fun deletePetsByOwner(
        ownerId: String
    )

    @Transaction
    suspend fun replacePetsByOwner(
        ownerId: String,
        pets: List<PetEntity>
    ) {
        deletePetsByOwner(ownerId)
        insertPets(pets)
    }
}
