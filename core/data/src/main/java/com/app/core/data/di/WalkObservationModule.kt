package com.app.core.data.di

import com.app.core.data.repository.WalkObservationRepositoryImpl
import com.app.core.domain.repository.WalkObservationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class WalkObservationModule {
    @Binds
    @Singleton
    abstract fun bindWalkObservationRepository(
        implementation: WalkObservationRepositoryImpl
    ): WalkObservationRepository
}
