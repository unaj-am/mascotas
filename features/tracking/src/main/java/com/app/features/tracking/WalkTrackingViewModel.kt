package com.app.features.tracking

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.model.MatchedRoute
import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkLocation
import com.app.core.domain.model.WalkStatus
import com.app.core.domain.usecase.GetWalkByIdUseCase
import com.app.core.domain.usecase.MatchWalkRouteUseCase
import com.app.core.domain.usecase.ObserveWalkLocationHistoryUseCase
import com.app.core.domain.usecase.ObserveWalkLocationUseCase
import com.app.core.domain.usecase.auth.GetCurrentUserIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

data class WalkTrackingUiState(
    val walk: Walk? = null,
    val currentLocation: WalkLocation? = null,
    val locationHistory: List<WalkLocation> = emptyList(),
    val matchedRoute: MatchedRoute? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val locationErrorMessage: String? = null,
    val historyErrorMessage: String? = null,
    val isWalker: Boolean = false
)

@HiltViewModel
class WalkTrackingViewModel @Inject constructor(
    private val getWalkById: GetWalkByIdUseCase,
    private val getCurrentUserId: GetCurrentUserIdUseCase,
    private val observeWalkLocation: ObserveWalkLocationUseCase,
    private val observeWalkLocationHistory: ObserveWalkLocationHistoryUseCase,
    private val matchWalkRoute: MatchWalkRouteUseCase
) : ViewModel() {

    companion object {
        private const val MIN_NEW_POINTS_FOR_MATCHING = 5
        private const val MATCHING_INTERVAL_MILLIS = 20_000L
    }

    private val _uiState = MutableStateFlow(WalkTrackingUiState())
    val uiState = _uiState.asStateFlow()

    private var currentWalkId: String = ""

    private var loadJob: Job? = null
    private var locationJob: Job? = null
    private var historyJob: Job? = null
    private var matchingJob: Job? = null

    private var lastMatchingPointTimestamp: Long? = null
    private var lastMatchingRequestTime: Long? = null

    fun load(
        walkId: String,
        forceRefresh: Boolean = false
    ) {
        if (walkId.isBlank()) {
            cancelTrackingJobs()
            currentWalkId = ""

            _uiState.value = WalkTrackingUiState(
                isLoading = false,
                errorMessage = "No se recibió el identificador del paseo."
            )
            return
        }

        val walkChanged = currentWalkId != walkId
        currentWalkId = walkId

        if (walkChanged) {
            cancelTrackingJobs()
            resetMatchingState()

            _uiState.value = WalkTrackingUiState(
                isLoading = true
            )
        } else if (
            !forceRefresh &&
            loadJob?.isActive == true
        ) {
            return
        } else if (
            !forceRefresh &&
            _uiState.value.walk?.id == walkId &&
            _uiState.value.errorMessage == null
        ) {
            return
        }

        loadJob?.cancel()

        loadJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val walk = getWalkById(walkId)
                    ?: throw IllegalStateException(
                        "No se encontró el paseo."
                    )

                if (currentWalkId != walkId) {
                    return@launch
                }

                val currentUserId = getCurrentUserId()

                _uiState.value = _uiState.value.copy(
                    walk = walk,
                    isLoading = false,
                    errorMessage = null,
                    isWalker = currentUserId != null &&
                            walk.walkerId == currentUserId
                )

                when (walk.status) {
                    WalkStatus.IN_PROGRESS -> {
                        observeLocation(walkId)
                        observeHistory(
                            walkId = walkId,
                            enableMatching = true
                        )
                    }

                    WalkStatus.COMPLETED,
                    WalkStatus.CANCELLED -> {
                        // El paseo terminó: ya no necesitamos observar
                        // la ubicación actual ni solicitar nuevos ajustes OSRM.
                        locationJob?.cancel()
                        locationJob = null

                        resetMatchingState()

                        _uiState.value = _uiState.value.copy(
                            currentLocation = null,
                            matchedRoute = null,
                            locationErrorMessage = null
                        )

                        // Conservamos la observación del historial GPS.
                        observeHistory(
                            walkId = walkId,
                            enableMatching = false
                        )
                    }

                    WalkStatus.SCHEDULED -> {
                        cancelTrackingJobs()
                        resetMatchingState()

                        _uiState.value = _uiState.value.copy(
                            currentLocation = null,
                            locationHistory = emptyList(),
                            matchedRoute = null,
                            locationErrorMessage = null,
                            historyErrorMessage = null
                        )
                    }
                }

            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                if (currentWalkId == walkId) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message
                            ?: "No se pudo consultar el paseo."
                    )
                }
            }
        }
    }

    private fun observeLocation(walkId: String) {
        locationJob?.cancel()

        locationJob = viewModelScope.launch {
            try {
                observeWalkLocation(walkId).collect { location ->
                    if (currentWalkId != walkId) {
                        return@collect
                    }

                    _uiState.value = _uiState.value.copy(
                        currentLocation = location,
                        locationErrorMessage = null
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                if (currentWalkId == walkId) {
                    _uiState.value = _uiState.value.copy(
                        locationErrorMessage = error.message
                            ?: "No se pudo recibir la ubicación del paseador."
                    )
                }
            }
        }
    }

    private fun observeHistory(
        walkId: String,
        enableMatching: Boolean
    ) {
        historyJob?.cancel()

        historyJob = viewModelScope.launch {
            try {
                observeWalkLocationHistory(walkId).collect { locations ->
                    if (currentWalkId != walkId) {
                        return@collect
                    }

                    _uiState.value = _uiState.value.copy(
                        locationHistory = locations,
                        historyErrorMessage = null
                    )

                    if (enableMatching) {
                        requestRouteMatching(
                            walkId = walkId,
                            locations = locations
                        )
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                if (currentWalkId == walkId) {
                    _uiState.value = _uiState.value.copy(
                        historyErrorMessage = error.message
                            ?: "No se pudo recuperar el recorrido."
                    )
                }
            }
        }
    }

    private fun requestRouteMatching(
        walkId: String,
        locations: List<WalkLocation>
    ) {
        if (matchingJob?.isActive == true) return

        val orderedLocations = locations
            .distinctBy { it.recordedAt }
            .sortedBy { it.recordedAt }

        if (orderedLocations.size < MIN_NEW_POINTS_FOR_MATCHING) {
            return
        }

        val lastRequestedTimestamp = lastMatchingPointTimestamp

        val newLocations = if (lastRequestedTimestamp == null) {
            orderedLocations
        } else {
            orderedLocations.filter {
                it.recordedAt > lastRequestedTimestamp
            }
        }

        if (newLocations.size < MIN_NEW_POINTS_FOR_MATCHING) {
            return
        }

        val now = SystemClock.elapsedRealtime()
        val lastRequestTime = lastMatchingRequestTime

        if (
            lastRequestTime != null &&
            now - lastRequestTime < MATCHING_INTERVAL_MILLIS
        ) {
            return
        }

        val requestLastTimestamp =
            orderedLocations.last().recordedAt

        // Registramos el intento antes de iniciar la petición.
        // Así evitamos repetir solicitudes ante cada emisión.
        lastMatchingPointTimestamp = requestLastTimestamp
        lastMatchingRequestTime = now

        matchingJob = viewModelScope.launch {
            try {
                val matchedRoute = matchWalkRoute(orderedLocations)

                if (
                    currentWalkId == walkId &&
                    matchedRoute != null
                ) {
                    _uiState.value = _uiState.value.copy(
                        matchedRoute = matchedRoute
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                // Si OSRM falla, se conserva la geometría anterior.
                // La pantalla podrá utilizar el historial GPS como fallback.
            }
        }
    }

    private fun resetMatchingState() {
        matchingJob?.cancel()
        matchingJob = null

        lastMatchingPointTimestamp = null
        lastMatchingRequestTime = null
    }

    private fun cancelTrackingJobs() {
        locationJob?.cancel()
        historyJob?.cancel()
        matchingJob?.cancel()

        locationJob = null
        historyJob = null
        matchingJob = null
    }

    fun refresh() {
        if (currentWalkId.isBlank()) return

        load(
            walkId = currentWalkId,
            forceRefresh = true
        )
    }

    override fun onCleared() {
        loadJob?.cancel()
        cancelTrackingJobs()
        super.onCleared()
    }
}