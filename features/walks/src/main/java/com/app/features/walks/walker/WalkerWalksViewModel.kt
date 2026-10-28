package com.app.features.walks.walker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.model.WalkStatus
import com.app.core.domain.usecase.CompleteWalkUseCase
import com.app.core.domain.usecase.GetWalksByWalkerUseCase
import com.app.core.domain.usecase.StartWalkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class WalkerWalksViewModel @Inject constructor(
    private val getWalksByWalker: GetWalksByWalkerUseCase,
    private val startWalk: StartWalkUseCase,
    private val completeWalk: CompleteWalkUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(WalkerWalksUiState())
    val uiState = _uiState.asStateFlow()
    private var walkerId: String = ""

    fun load(walkerId: String, forceRefresh: Boolean = false) {
        if (walkerId.isBlank()) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "No se pudo identificar al paseador.") }
            return
        }
        this.walkerId = walkerId
        if (!forceRefresh && !_uiState.value.isLoading && _uiState.value.errorMessage == null && _uiState.value.walks.isNotEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, operationError = null) }
            try {
                val walks = getWalksByWalker(walkerId).sortedBy { it.scheduledAt }
                _uiState.update { it.copy(walks = walks, isLoading = false, errorMessage = null) }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message ?: "No se pudieron cargar los paseos.") }
            }
        }
    }

    fun refresh() = load(walkerId, forceRefresh = true)
    fun start(walkId: String) = perform(walkId, "El paseo comenzó.") { startWalk(walkId) }
    fun complete(walkId: String) = perform(walkId, "El paseo se marcó como finalizado.") { completeWalk(walkId) }

    private fun perform(id: String, successMessage: String, operation: suspend () -> Unit) {
        if (_uiState.value.processingId != null) return
        viewModelScope.launch {
            _uiState.update { it.copy(processingId = id, operationMessage = null, operationError = null) }
            try {
                operation()
                _uiState.update { it.copy(processingId = null, operationMessage = successMessage) }
                load(walkerId, forceRefresh = true)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update { it.copy(processingId = null, operationError = error.message ?: "No se pudo actualizar el paseo.") }
            }
        }
    }
}
