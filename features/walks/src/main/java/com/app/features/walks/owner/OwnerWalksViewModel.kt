
package com.app.features.walks.owner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.model.RequestStatus
import com.app.core.domain.model.WalkStatus
import com.app.core.domain.usecase.CancelWalkUseCase
import com.app.core.domain.usecase.GetWalkRequestsByOwnerUseCase
import com.app.core.domain.usecase.GetWalksByOwnerUseCase
import com.app.core.domain.usecase.UpdateWalkRequestStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class OwnerWalksViewModel @Inject constructor(
    private val getWalksByOwner: GetWalksByOwnerUseCase,
    private val getRequestsByOwner: GetWalkRequestsByOwnerUseCase,
    private val cancelWalk: CancelWalkUseCase,
    private val updateRequestStatus: UpdateWalkRequestStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OwnerWalksUiState())
    val uiState = _uiState.asStateFlow()

    private var ownerId: String = ""
    private var loadJob: Job? = null

    fun load(
        ownerId: String,
        forceRefresh: Boolean = false
    ) {
        if (ownerId.isBlank()) {
            loadJob?.cancel()
            this.ownerId = ""

            _uiState.update {
                it.copy(
                    walks = emptyList(),
                    requests = emptyList(),
                    isLoading = false,
                    errorMessage = "No se pudo identificar al dueño."
                )
            }
            return
        }

        val ownerChanged = this.ownerId != ownerId
        this.ownerId = ownerId

        if (ownerChanged) {
            loadJob?.cancel()

            _uiState.update {
                it.copy(
                    walks = emptyList(),
                    requests = emptyList(),
                    isLoading = true,
                    errorMessage = null,
                    operationMessage = null,
                    operationError = null,
                    processingId = null
                )
            }
        }

        if (!forceRefresh) {
            if (loadJob?.isActive == true) return

            val currentState = _uiState.value
            val hasData = currentState.walks.isNotEmpty() ||
                    currentState.requests.isNotEmpty()

            if (
                !currentState.isLoading &&
                currentState.errorMessage == null &&
                hasData
            ) {
                return
            }
        }

        loadJob?.cancel()

        loadJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    operationError = null
                )
            }

            try {
                val walks = getWalksByOwner(ownerId)
                    .sortedByDescending { it.scheduledAt }

                val requests = getRequestsByOwner(ownerId)
                    .filter { request ->
                        request.status != RequestStatus.ACCEPTED
                    }
                    .sortedByDescending { it.scheduledAt }

                // Evita aplicar datos de un dueño anterior.
                if (this@OwnerWalksViewModel.ownerId != ownerId) {
                    return@launch
                }

                _uiState.update {
                    it.copy(
                        walks = walks,
                        requests = requests,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                if (this@OwnerWalksViewModel.ownerId == ownerId) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message
                                ?: "No se pudieron cargar tus paseos."
                        )
                    }
                }
            }
        }
    }

    fun refresh() {
        if (ownerId.isBlank()) return
        load(ownerId, forceRefresh = true)
    }

    fun cancelRequest(requestId: String) {
        perform(
            id = requestId,
            successMessage = "La solicitud fue cancelada."
        ) {
            updateRequestStatus(
                requestId,
                RequestStatus.CANCELLED
            )
        }
    }
    fun selectFilter(filter: OwnerWalkFilter) {
        _uiState.update {
            it.copy(selectedFilter = filter)
        }
    }
    fun cancelScheduledWalk(walkId: String) {
        perform(
            id = walkId,
            successMessage = "El paseo fue cancelado."
        ) {
            cancelWalk(walkId)
        }
    }

    private fun perform(
        id: String,
        successMessage: String,
        operation: suspend () -> Unit
    ) {
        if (_uiState.value.processingId != null) return

        val currentOwnerId = ownerId
        if (currentOwnerId.isBlank()) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    processingId = id,
                    operationMessage = null,
                    operationError = null
                )
            }

            try {
                operation()

                _uiState.update {
                    it.copy(
                        processingId = null,
                        operationMessage = successMessage
                    )
                }

                if (ownerId == currentOwnerId) {
                    load(
                        ownerId = currentOwnerId,
                        forceRefresh = true
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        processingId = null,
                        operationError = error.message
                            ?: "No se pudo completar la operación."
                    )
                }
            }
        }
    }
}
