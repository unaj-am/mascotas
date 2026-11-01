
package com.app.core.data.datasource

import com.app.core.domain.model.MatchedRoute
import com.app.core.domain.model.MatchedRouteSegment
import com.app.core.domain.model.RouteCoordinate
import com.app.core.domain.model.WalkLocation
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class OsrmMapMatchingRemoteDataSource @Inject constructor() {

    companion object {
        private const val BASE_URL = "https://router.project-osrm.org"

        // El perfil debe existir en el servidor OSRM configurado.
        // El servidor público de demostración utiliza driving.
        private const val PROFILE = "driving"

        private const val CONNECT_TIMEOUT = 10_000
        private const val READ_TIMEOUT = 15_000

        private const val MIN_COORDINATES = 2
    }

    /**
     * Envía una secuencia de ubicaciones GPS a OSRM
     * y devuelve la geometría ajustada a la red vial.
     */
    suspend fun matchRoute(
        locations: List<WalkLocation>
    ): MatchedRoute? = withContext(Dispatchers.IO) {

        val validLocations = locations
            .distinctBy { it.recordedAt }
            .sortedBy { it.recordedAt }

        if (validLocations.size < MIN_COORDINATES) {
            return@withContext null
        }

        val requestBody = buildRequestBody(validLocations)

        val connection = (
                URL("$BASE_URL/match/v1/$PROFILE").openConnection()
                        as HttpURLConnection
                )

        try {
            connection.requestMethod = "POST"
            connection.connectTimeout = CONNECT_TIMEOUT
            connection.readTimeout = READ_TIMEOUT
            connection.doOutput = true

            connection.setRequestProperty(
                "Content-Type",
                "application/json; charset=utf-8"
            )

            connection.setRequestProperty(
                "Accept",
                "application/json"
            )

            connection.outputStream.use { output ->
                output.write(requestBody.toString().toByteArray(Charsets.UTF_8))
            }

            val responseCode = connection.responseCode

            val responseStream = if (responseCode in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }

            val responseText = responseStream?.bufferedReader()?.use {
                it.readText()
            }.orEmpty()

            if (responseCode !in 200..299) {
                throw IOException(
                    "OSRM respondió con HTTP $responseCode: $responseText"
                )
            }

            parseResponse(responseText)

        } finally {
            connection.disconnect()
        }
    }

    /**
     * Construye el JSON requerido por el endpoint Match de OSRM.
     *
     * Las coordenadas se envían en orden:
     * [longitud, latitud].
     */
    private fun buildRequestBody(
        locations: List<WalkLocation>
    ): JSONObject {

        val coordinates = JSONArray()
        val timestamps = JSONArray()
        val radiuses = JSONArray()

        locations.forEach { location ->

            coordinates.put(
                JSONArray().apply {
                    put(location.longitude)
                    put(location.latitude)
                }
            )

            // OSRM requiere timestamps en segundos Unix.
            timestamps.put(location.recordedAt / 1_000L)

            // Utilizamos la precisión GPS como radio de incertidumbre.
            radiuses.put(
                location.accuracy
                    .toDouble()
                    .coerceIn(5.0, 50.0)
            )
        }

        return JSONObject().apply {
            put("coordinates", coordinates)
            put("timestamps", timestamps)
            put("radiuses", radiuses)

            put("geometries", "geojson")
            put("overview", "full")
            put("steps", false)

            put("gaps", "split")
            put("tidy", true)
        }
    }

    /**
     * Convierte la respuesta JSON de OSRM en nuestro
     * modelo de dominio MatchedRoute.
     */
    private fun parseResponse(
        responseText: String
    ): MatchedRoute? {

        if (responseText.isBlank()) {
            return null
        }

        val response = JSONObject(responseText)

        if (response.optString("code") != "Ok") {
            return null
        }

        val matchings = response.optJSONArray("matchings")
            ?: return null

        val segments = mutableListOf<MatchedRouteSegment>()

        for (index in 0 until matchings.length()) {

            val matching = matchings.optJSONObject(index)
                ?: continue

            val geometry = matching.optJSONObject("geometry")
                ?: continue

            val coordinates = geometry.optJSONArray("coordinates")
                ?: continue

            val routeCoordinates = mutableListOf<RouteCoordinate>()

            for (coordinateIndex in 0 until coordinates.length()) {

                val coordinate = coordinates.optJSONArray(coordinateIndex)
                    ?: continue

                if (coordinate.length() < 2) {
                    continue
                }

                val longitude = coordinate.optDouble(0, Double.NaN)
                val latitude = coordinate.optDouble(1, Double.NaN)

                if (
                    longitude.isNaN() ||
                    latitude.isNaN()
                ) {
                    continue
                }

                if (
                    latitude !in -90.0..90.0 ||
                    longitude !in -180.0..180.0
                ) {
                    continue
                }

                routeCoordinates.add(
                    RouteCoordinate(
                        latitude = latitude,
                        longitude = longitude
                    )
                )
            }

            if (routeCoordinates.size < 2) {
                continue
            }

            val confidence = if (matching.has("confidence")) {
                matching.optDouble("confidence").takeIf {
                    !it.isNaN() && it in 0.0..1.0
                }
            } else {
                null
            }

            segments.add(
                MatchedRouteSegment(
                    coordinates = routeCoordinates,
                    confidence = confidence
                )
            )
        }

        return MatchedRoute(segments)
            .takeIf { it.hasGeometry }
    }
}
