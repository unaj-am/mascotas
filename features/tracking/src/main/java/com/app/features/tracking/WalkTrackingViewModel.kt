
package com.app.features.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.model.Walk
import com.app.core.domain.usecase.GetWalkByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
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
    private var loadJob: Job? = null

    fun load(
        walkId: String,
        forceRefresh: Boolean = false
    ) {
        if (walkId.isBlank()) {
            loadJob?.cancel()
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

                _uiState.value = WalkTrackingUiState(
                    walk = walk,
                    isLoading = false
                )
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

    fun refresh() {
        if (currentWalkId.isBlank()) return

        load(
            walkId = currentWalkId,
            forceRefresh = true
        )
    }
}
