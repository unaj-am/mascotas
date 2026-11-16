package com.app.cloudinary.di

import com.app.cloudinary.uploader.CloudinaryPhotoUploader
import com.app.core.domain.service.PhotoUploader
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CloudinaryModule {
    @Binds
    @Singleton
    abstract fun bindPhotoUploader(
        implementation: CloudinaryPhotoUploader
    ): PhotoUploader
}
