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

    fun load(ownerId: String, forceRefresh: Boolean = false) {
        if (ownerId.isBlank()) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "No se pudo identificar al dueño.") }
            return
        }
        this.ownerId = ownerId
        if (!forceRefresh && !_uiState.value.isLoading && _uiState.value.errorMessage == null && (_uiState.value.walks.isNotEmpty() || _uiState.value.requests.isNotEmpty())) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, operationError = null) }
            try {
                val walks = getWalksByOwner(ownerId).sortedByDescending { it.scheduledAt }
                val requests = getRequestsByOwner(ownerId)
                    .filter { it.status != RequestStatus.ACCEPTED }
                    .sortedByDescending { it.scheduledAt }
                _uiState.update { it.copy(walks = walks, requests = requests, isLoading = false, errorMessage = null) }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message ?: "No se pudieron cargar tus paseos.") }
            }
        }
    }

    fun refresh() = load(ownerId, forceRefresh = true)

    fun cancelRequest(requestId: String) = perform(requestId, "La solicitud fue cancelada.") {
        updateRequestStatus(requestId, RequestStatus.CANCELLED)
    }

    fun cancelScheduledWalk(walkId: String) = perform(walkId, "El paseo fue cancelado.") {
        cancelWalk(walkId)
    }

    private fun perform(id: String, successMessage: String, operation: suspend () -> Unit) {
        if (_uiState.value.processingId != null) return
        viewModelScope.launch {
            _uiState.update { it.copy(processingId = id, operationMessage = null, operationError = null) }
            try {
                operation()
                _uiState.update { it.copy(processingId = null, operationMessage = successMessage) }
                load(ownerId, forceRefresh = true)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update { it.copy(processingId = null, operationError = error.message ?: "No se pudo completar la operación.") }
            }
        }
    }
}
