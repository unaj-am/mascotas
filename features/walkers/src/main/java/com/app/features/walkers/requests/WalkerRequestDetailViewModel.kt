
package com.app.features.walkers.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.usecase.GetWalkRequestByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class WalkerRequestDetailViewModel @Inject constructor(
    private val getWalkRequestByIdUseCase: GetWalkRequestByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalkerRequestDetailUiState())

    val uiState = _uiState.asStateFlow()

    fun loadRequest(requestId: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            try {
                val request = getWalkRequestByIdUseCase(requestId)

                if (request == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "No se encontró la solicitud"
                        )
                    }
                    return@launch
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        requestId = request.id,
                        ownerName = request.ownerName,
                        petNames = request.petNames,
                        petPhotos = request.petPhotos,
                        scheduledAt = request.scheduledAt,
                        durationMinutes = request.durationMinutes,
                        observations = request.observations,
                        status = request.status.name,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                            ?: "No se pudo cargar la solicitud"
                    )
                }
            }
        }
    }
}
