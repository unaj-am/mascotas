
package com.app.core.data.di

import com.app.core.data.repository.NotificationRepositoryImpl
import com.app.core.domain.repository.NotificationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        implementation: NotificationRepositoryImpl
    ): NotificationRepository
}
