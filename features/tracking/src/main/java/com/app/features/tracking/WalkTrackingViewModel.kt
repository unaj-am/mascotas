package com.app.features.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.model.Walk
import com.app.core.domain.usecase.GetWalkByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WalkTrackingUiState(
    val walk: Walk? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class WalkTrackingViewModel @Inject constructor(
    private val getWalkById: GetWalkByIdUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(WalkTrackingUiState())
    val uiState = _uiState.asStateFlow()
    private var currentWalkId: String = ""

    fun load(walkId: String, forceRefresh: Boolean = false) {
        if (walkId.isBlank()) {
            _uiState.value = WalkTrackingUiState(isLoading = false, errorMessage = "No se recibió el identificador del paseo.")
            return
        }
        currentWalkId = walkId
        if (!forceRefresh && _uiState.value.walk?.id == walkId && _uiState.value.errorMessage == null) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val walk = getWalkById(walkId) ?: throw IllegalStateException("No se encontró el paseo.")
                _uiState.value = WalkTrackingUiState(walk = walk, isLoading = false)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.value = WalkTrackingUiState(isLoading = false, errorMessage = error.message ?: "No se pudo consultar el paseo.")
            }
        }
    }

    fun refresh() = load(currentWalkId, forceRefresh = true)
}
