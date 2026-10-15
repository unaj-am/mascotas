package com.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.core.database.dao.PetDao
import com.app.core.database.model.PetEntity

@Database(
    entities = [PetEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun petDao(): PetDao
}
