
package com.app.core.data.di

import com.app.core.data.repository.AuthRepositoryImpl
import com.app.core.data.repository.PetRepositoryImpl
import com.app.core.data.repository.UserRepositoryImpl
import com.app.core.domain.repository.AuthRepository
import com.app.core.domain.repository.PetRepository
import com.app.core.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        implementation: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        implementation: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindPetRepository(
        implementation: PetRepositoryImpl
    ): PetRepository
}
