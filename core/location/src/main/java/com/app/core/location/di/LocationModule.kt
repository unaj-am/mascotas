
package com.app.core.location.di

import com.app.core.location.FusedLocationProvider
import com.app.core.location.LocationProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de inyección de dependencias para la ubicación.
 *
 * Vincula la interfaz LocationProvider con su implementación
 * concreta FusedLocationProvider.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {

    /**
     * Permite que Hilt entregue FusedLocationProvider
     * cuando algún componente solicite LocationProvider.
     */
    @Binds
    @Singleton
    abstract fun bindLocationProvider(
        implementation: FusedLocationProvider
    ): LocationProvider
}
