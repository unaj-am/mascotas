
package com.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.app.core.database.dao.PetDao
import com.app.core.database.dao.UserDao
import com.app.core.database.dao.WalkerDao
import com.app.core.database.model.PetEntity
import com.app.core.database.model.UserEntity
import com.app.core.database.model.WalkerEntity

@Database(
    entities = [
        PetEntity::class,
        UserEntity::class,
        WalkerEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun petDao(): PetDao

    abstract fun userDao(): UserDao

    abstract fun walkerDao(): WalkerDao

    companion object {

        val MIGRATION_1_2 = object : Migration(1, 2) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `users` (
                        `id` TEXT NOT NULL,
                        `name` TEXT NOT NULL,
                        `lastName` TEXT NOT NULL,
                        `email` TEXT NOT NULL,
                        `phone` TEXT NOT NULL,
                        `profileImage` TEXT,
                        `role` TEXT NOT NULL,
                        `active` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `walkers` (
                        `userId` TEXT NOT NULL,
                        `description` TEXT NOT NULL,
                        `experienceYears` INTEGER NOT NULL,
                        `serviceArea` TEXT NOT NULL,
                        `available` INTEGER NOT NULL,
                        `pricePerWalk` REAL NOT NULL,
                        `maxPets` INTEGER NOT NULL,
                        `rating` REAL,
                        PRIMARY KEY(`userId`)
                    )
                    """.trimIndent()
                )
            }
        }
    }
}
