package com.app.features.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkLocation
import com.app.core.domain.model.WalkStatus
import com.app.core.domain.usecase.GetWalkByIdUseCase
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
    private val observeWalkLocationHistory: ObserveWalkLocationHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalkTrackingUiState())
    val uiState = _uiState.asStateFlow()

    private var currentWalkId: String = ""
    private var loadJob: Job? = null
    private var locationJob: Job? = null
    private var historyJob: Job? = null

    fun load(
        walkId: String,
        forceRefresh: Boolean = false
    ) {
        if (walkId.isBlank()) {
            loadJob?.cancel()
            locationJob?.cancel()
            historyJob?.cancel()

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
            loadJob?.cancel()
            locationJob?.cancel()
            historyJob?.cancel()

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

                if (walk.status == WalkStatus.IN_PROGRESS) {
                    observeLocation(walkId)
                    observeHistory(walkId)
                } else {
                    locationJob?.cancel()
                    historyJob?.cancel()

                    locationJob = null
                    historyJob = null

                    _uiState.value = _uiState.value.copy(
                        currentLocation = null,
                        locationHistory = emptyList(),
                        locationErrorMessage = null,
                        historyErrorMessage = null
                    )
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

    private fun observeHistory(walkId: String) {
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

    fun refresh() {
        if (currentWalkId.isBlank()) return

        load(
            walkId = currentWalkId,
            forceRefresh = true
        )
    }

    override fun onCleared() {
        loadJob?.cancel()
        locationJob?.cancel()
        historyJob?.cancel()
        super.onCleared()
    }
}