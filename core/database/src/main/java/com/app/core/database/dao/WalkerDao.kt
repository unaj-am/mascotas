
package com.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.app.core.database.model.WalkerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WalkerDao {

    @Query("""
        SELECT * FROM walkers
        WHERE userId = :userId
        LIMIT 1
    """)
    suspend fun getWalkerById(
        userId: String
    ): WalkerEntity?

    @Query("""
        SELECT * FROM walkers
        WHERE userId = :userId
        LIMIT 1
    """)
    fun observeWalkerById(
        userId: String
    ): Flow<WalkerEntity?>

    @Query("""
        SELECT * FROM walkers
        ORDER BY rating DESC
    """)
    fun getAllWalkers(): Flow<List<WalkerEntity>>

    @Query("""
        SELECT * FROM walkers
        WHERE available = 1
        ORDER BY rating DESC
    """)
    fun getAvailableWalkers(): Flow<List<WalkerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWalker(
        walker: WalkerEntity
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWalkers(
        walkers: List<WalkerEntity>
    )

    @Query("UPDATE walkers SET available = 0")
    suspend fun markAllWalkersUnavailable()

    @Transaction
    suspend fun replaceAllWalkers(
        walkers: List<WalkerEntity>
    ) {
        deleteAllWalkers()
        insertWalkers(walkers)
    }

    @Transaction
    suspend fun replaceAvailableWalkers(
        walkers: List<WalkerEntity>
    ) {
        markAllWalkersUnavailable()
        insertWalkers(walkers)
    }

    @Query("DELETE FROM walkers WHERE userId = :userId")
    suspend fun deleteWalkerById(
        userId: String
    )

    @Query("DELETE FROM walkers")
    suspend fun deleteAllWalkers()
}
