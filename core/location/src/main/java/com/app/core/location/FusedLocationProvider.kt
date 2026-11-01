
package com.app.core.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import com.app.core.location.model.LocationData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Implementación de LocationProvider utilizando
 * Google Play Services - Fused Location Provider.
 *
 * Es responsable exclusivamente de obtener las coordenadas
 * del dispositivo y transformarlas en LocationData.
 */
@Singleton
class FusedLocationProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationProvider {

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * Comprueba si la aplicación tiene algún permiso
     * de ubicación concedido.
     *
     * Se admite ubicación precisa o aproximada.
     */
    private fun hasLocationPermission(): Boolean {

        val fineLocationGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        return fineLocationGranted || coarseLocationGranted
    }

    /**
     * Comienza a emitir actualizaciones GPS.
     *
     * Las actualizaciones se detienen automáticamente
     * cuando se cancela la colección del Flow.
     */
    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(
        intervalMillis: Long,
        fastestIntervalMillis: Long
    ): Flow<LocationData> = callbackFlow {

        require(intervalMillis > 0L) {
            "El intervalo de ubicación debe ser mayor a cero."
        }

        require(fastestIntervalMillis > 0L) {
            "El intervalo mínimo debe ser mayor a cero."
        }

        require(fastestIntervalMillis <= intervalMillis) {
            "El intervalo mínimo no puede superar el intervalo principal."
        }

        // Verificamos permisos antes de acceder al GPS.
        if (!hasLocationPermission()) {
            close(
                SecurityException(
                    "La aplicación no tiene permisos de ubicación."
                )
            )
            return@callbackFlow
        }

        // Configuración de las actualizaciones GPS.
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            intervalMillis
        )
            .setMinUpdateIntervalMillis(fastestIntervalMillis)
            .build()

        // Callback que recibe las nuevas ubicaciones.
        val locationCallback = object : LocationCallback() {

            override fun onLocationResult(
                locationResult: LocationResult
            ) {
                for (location in locationResult.locations) {

                    val locationData = location.toLocationData()

                    trySend(locationData)
                }
            }
        }

        // Registramos el callback en Google Play Services.
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        ).addOnFailureListener { exception ->
            close(exception)
        }

        // Cuando se cancela el Flow, eliminamos el callback.
        awaitClose {
            fusedLocationClient.removeLocationUpdates(
                locationCallback
            )
        }
    }

    /**
     * Convierte la ubicación proporcionada por Android
     * en nuestro modelo independiente LocationData.
     */
    private fun Location.toLocationData(): LocationData {

        return LocationData(
            latitude = latitude,
            longitude = longitude,

            accuracy = if (hasAccuracy()) {
                accuracy
            } else {
                0f
            },

            bearing = if (hasBearing()) {
                bearing.takeIf { it in 0f..360f }
            } else {
                null
            },

            speed = if (hasSpeed()) {
                speed.takeIf { it >= 0f }
            } else {
                null
            },

            timestamp = time.takeIf { it > 0L }
                ?: System.currentTimeMillis()
        )
    }
}
