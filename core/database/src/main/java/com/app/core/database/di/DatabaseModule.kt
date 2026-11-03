
package com.app.core.database.di

import android.content.Context
import androidx.room.Room
import com.app.core.database.AppDatabase
import com.app.core.database.dao.PetDao
import com.app.core.database.dao.UserDao
import com.app.core.database.dao.WalkerDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {

        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "mascotas_database"
        )
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()
    }

    @Provides
    fun providePetDao(
        database: AppDatabase
    ): PetDao {
        return database.petDao()
    }

    @Provides
    fun provideUserDao(
        database: AppDatabase
    ): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideWalkerDao(
        database: AppDatabase
    ): WalkerDao {
        return database.walkerDao()
    }
}
