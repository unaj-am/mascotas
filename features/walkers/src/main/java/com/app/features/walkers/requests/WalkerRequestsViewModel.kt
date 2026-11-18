
package com.app.features.walkers.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.model.RequestStatus
import com.app.core.domain.model.WalkRequest
import com.app.core.domain.usecase.AcceptWalkRequestUseCase
import com.app.core.domain.repository.WalkObservationRepository
import com.app.core.domain.usecase.UpdateWalkRequestStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class WalkerRequestsViewModel @Inject constructor(
    private val walkObservationRepository: WalkObservationRepository,
    private val acceptWalkRequest: AcceptWalkRequestUseCase,
    private val updateRequestStatus: UpdateWalkRequestStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        WalkerRequestsUiState()
    )

    val uiState: StateFlow<WalkerRequestsUiState> =
        _uiState.asStateFlow()

    private var requestsJob: Job? = null

    fun loadRequests(walkerId: String) {
        requestsJob?.cancel()
        if (walkerId.isBlank()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    loadingError =
                        "No se recibió el identificador del paseador."
                )
            }
            return
        }

        _uiState.update {
            it.copy(
                walkerId = walkerId,
                isLoading = true,
                loadingError = null,
                operationError = null,
                operationMessage = null
            )
        }

        requestsJob = viewModelScope.launch {
            try {
                walkObservationRepository.observeRequestsByWalker(walkerId)
                    .collect { requests ->
                        if (_uiState.value.walkerId != walkerId) return@collect
                        val displayNames = loadDisplayNames(requests)
                        _uiState.update {
                            it.copy(
                                requests = requests,
                                ownerNames = displayNames.first,
                                petNames = displayNames.second,
                                isLoading = false,
                                loadingError = null
                            )
                        }
                    }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        loadingError = exception.message
                            ?: "No se pudieron cargar las solicitudes."
                    )
                }
            }
        }
    }

    fun retryLoadRequests() {
        val walkerId = _uiState.value.walkerId

        if (walkerId.isNotBlank()) {
            loadRequests(walkerId)
        }
    }

    fun selectFilter(filter: WalkerRequestFilter) {
        _uiState.update {
            it.copy(selectedFilter = filter)
        }
    }

    fun acceptRequest(requestId: String) {
        processRequest(requestId) {
            acceptWalkRequest(requestId)
            "La solicitud fue aceptada y el paseo quedó registrado."
        }
    }

    fun rejectRequest(requestId: String) {
        processRequest(requestId) {
            updateRequestStatus(
                requestId = requestId,
                newStatus = RequestStatus.REJECTED
            )
            "La solicitud fue rechazada."
        }
    }

    private fun processRequest(
        requestId: String,
        operation: suspend () -> String
    ) {
        val currentState = _uiState.value

        if (
            currentState.walkerId.isBlank() ||
            currentState.processingRequestId != null
        ) {
            return
        }

        _uiState.update {
            it.copy(
                processingRequestId = requestId,
                operationMessage = null,
                operationError = null,
                loadingError = null
            )
        }

        viewModelScope.launch {
            try {
                val message = operation()
                if (_uiState.value.walkerId != currentState.walkerId) return@launch

                _uiState.update {
                    it.copy(
                        processingRequestId = null,
                        operationMessage = message,
                        operationError = null,
                        loadingError = null
                    )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        processingRequestId = null,
                        operationError = exception.message
                            ?: "No se pudo procesar la solicitud."
                    )
                }
            }
        }
    }

    private fun loadDisplayNames(
        requests: List<WalkRequest>
    ): Pair<Map<String, String>, Map<String, List<String>>> {
        val ownerNames = requests.associate { request ->
            request.id to request.ownerName
                .takeIf { it.isNotBlank() }
                .orEmpty()
                .ifBlank { "Dueño no disponible" }
        }

        val petNames = requests.associate { request ->
            request.id to request.petNames.map { name ->
                name.takeIf { it.isNotBlank() }
                    ?: "Mascota no disponible"
            }.ifEmpty {
                request.petIds.map {
                    "Mascota no disponible"
                }
            }
        }

        return ownerNames to petNames
    }

    fun clearMessages() {
        _uiState.update {
            it.copy(
                operationMessage = null,
                operationError = null
            )
        }
    }
}
