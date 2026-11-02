
package com.app.core.data.datasource

import android.util.Log
import com.app.core.domain.model.MatchedRoute
import com.app.core.domain.model.MatchedRouteSegment
import com.app.core.domain.model.RouteCoordinate
import com.app.core.domain.model.WalkLocation
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class OsrmMapMatchingRemoteDataSource @Inject constructor() {

    companion object {
        private const val TAG = "OsrmMapMatching"

        private const val BASE_URL = "https://router.project-osrm.org"
        private const val PROFILE = "driving"

        private const val CONNECT_TIMEOUT = 10_000
        private const val READ_TIMEOUT = 15_000

        private const val MIN_COORDINATES = 2
    }

    suspend fun matchRoute(
        locations: List<WalkLocation>
    ): MatchedRoute? = withContext(Dispatchers.IO) {

        val validLocations = locations
            .distinctBy { it.recordedAt }
            .sortedBy { it.recordedAt }

        if (validLocations.size < MIN_COORDINATES) {
            return@withContext null
        }

        val requestUrl = buildRequestUrl(validLocations)

        val connection = URL(requestUrl).openConnection()
                as HttpURLConnection

        try {
            // OSRM Match utiliza GET, no POST con JSON.
            connection.requestMethod = "GET"
            connection.connectTimeout = CONNECT_TIMEOUT
            connection.readTimeout = READ_TIMEOUT
            connection.doInput = true
            connection.useCaches = false

            connection.setRequestProperty(
                "Accept",
                "application/json"
            )

            val responseCode = connection.responseCode

            val responseStream = if (responseCode in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }

            val responseText = responseStream
                ?.bufferedReader()
                ?.use { it.readText() }
                .orEmpty()

            if (responseCode !in 200..299) {
                Log.e(
                    TAG,
                    "Error HTTP $responseCode: $responseText"
                )

                throw IOException(
                    "OSRM respondió con HTTP $responseCode"
                )
            }

            parseResponse(responseText)

        } finally {
            connection.disconnect()
        }
    }

    /**
     * Construye la URL requerida por el endpoint Match de OSRM.
     *
     * Formato:
     * /match/v1/driving/{longitud,latitud;longitud,latitud}
     *
     * Los parámetros adicionales se envían mediante query string.
     */
    private fun buildRequestUrl(
        locations: List<WalkLocation>
    ): String {

        val coordinates = locations.joinToString(";") { location ->
            "${location.longitude},${location.latitude}"
        }

        val timestamps = locations.joinToString(";") { location ->
            (location.recordedAt / 1_000L).toString()
        }

        val radiuses = locations.joinToString(";") { location ->
            location.accuracy
                .toDouble()
                .coerceIn(5.0, 50.0)
                .toString()
        }

        val parameters = listOf(
            "timestamps" to timestamps,
            "radiuses" to radiuses,
            "geometries" to "geojson",
            "overview" to "full",
            "steps" to "false",
            "gaps" to "split",
            "tidy" to "true"
        )

        val queryString = parameters.joinToString("&") { (key, value) ->
            "$key=${URLEncoder.encode(value, Charsets.UTF_8.name())}"
        }

        return "$BASE_URL/match/v1/$PROFILE/$coordinates?$queryString"
    }

    /**
     * Convierte la respuesta JSON de OSRM en MatchedRoute.
     */
    private fun parseResponse(
        responseText: String
    ): MatchedRoute? {

        if (responseText.isBlank()) {
            Log.w(TAG, "OSRM devolvió una respuesta vacía.")
            return null
        }

        val response = JSONObject(responseText)
        val responseCode = response.optString("code")

        if (responseCode != "Ok") {
            val message = response.optString(
                "message",
                "Sin información adicional"
            )

            Log.w(
                TAG,
                "OSRM no pudo ajustar la ruta. Código: $responseCode. Detalle: $message"
            )

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
                    !longitude.isFinite() ||
                    !latitude.isFinite()
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
                    it.isFinite() && it in 0.0..1.0
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

        val matchedRoute = MatchedRoute(segments)
            .takeIf { it.hasGeometry }

        Log.d(
            TAG,
            "Resultado OSRM: ${matchedRoute?.segments?.size ?: 0} segmentos, " +
                    "${matchedRoute?.coordinateCount ?: 0} coordenadas."
        )

        return matchedRoute
    }
}
