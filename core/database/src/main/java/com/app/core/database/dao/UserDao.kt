
package com.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.core.database.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("""
        SELECT * FROM users
        WHERE id = :userId
        LIMIT 1
    """)
    suspend fun getUserById(
        userId: String
    ): UserEntity?

    @Query("""
        SELECT * FROM users
        WHERE id = :userId
        LIMIT 1
    """)
    fun observeUserById(
        userId: String
    ): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(
        user: UserEntity
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(
        users: List<UserEntity>
    )

    @Query("""
        UPDATE users
        SET profileImage = :imageUrl
        WHERE id = :userId
    """)
    suspend fun updateProfileImage(
        userId: String,
        imageUrl: String
    )

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserById(
        userId: String
    )
}
