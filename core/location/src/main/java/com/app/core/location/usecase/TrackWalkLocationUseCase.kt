package com.app.core.location.usecase

import com.app.core.domain.model.WalkLocation
import com.app.core.domain.usecase.PublishWalkLocationUseCase
import com.app.core.location.LocationProvider
import javax.inject.Inject
import kotlinx.coroutines.flow.collect

class TrackWalkLocationUseCase @Inject constructor(
    private val locationProvider: LocationProvider,
    private val publishWalkLocationUseCase: PublishWalkLocationUseCase
) {

    suspend operator fun invoke(walkId: String) {
        require(walkId.isNotBlank()) {
            "El identificador del paseo es obligatorio"
        }

        locationProvider.getLocationUpdates(
            intervalMillis = 5_000L,
            fastestIntervalMillis = 3_000L
        ).collect { locationData ->

            val walkLocation = WalkLocation(
                latitude = locationData.latitude,
                longitude = locationData.longitude,
                accuracy = locationData.accuracy,
                bearing = locationData.bearing,
                speed = locationData.speed,
                recordedAt = locationData.timestamp
            )

            publishWalkLocationUseCase(
                walkId = walkId,
                location = walkLocation
            )
        }
    }
}