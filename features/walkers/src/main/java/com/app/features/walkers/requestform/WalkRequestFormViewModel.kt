package com.app.features.walkers.requestform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.model.RequestStatus
import com.app.core.domain.model.WalkRequest
import com.app.core.domain.usecase.CreateWalkRequestUseCase
import com.app.core.domain.usecase.GetPetsByOwnerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
@HiltViewModel
class WalkRequestFormViewModel @Inject constructor(
    private val getPetsByOwnerUseCase: GetPetsByOwnerUseCase,
    private val createWalkRequestUseCase: CreateWalkRequestUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        WalkRequestFormUiState()
    )

    val uiState = _uiState.asStateFlow()

    fun loadPets(ownerId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val pets = getPetsByOwnerUseCase(ownerId)

                _uiState.value = _uiState.value.copy(
                    pets = pets,
                    isLoading = false
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                        ?: "No se pudieron cargar las mascotas."
                )
            }
        }
    }

    fun togglePet(petId: String) {
        val selected = _uiState.value
            .selectedPetIds
            .toMutableSet()

        if (!selected.add(petId)) {
            selected.remove(petId)
        }

        _uiState.value = _uiState.value.copy(
            selectedPetIds = selected,
            errorMessage = null
        )
    }

    fun updateScheduledAt(value: String) {
        _uiState.value = _uiState.value.copy(
            scheduledAt = value,
            errorMessage = null
        )
    }

    fun updateDuration(value: String) {
        _uiState.value = _uiState.value.copy(
            durationMinutes = value,
            errorMessage = null
        )
    }

    fun updateObservations(value: String) {
        _uiState.value = _uiState.value.copy(
            observations = value,
            errorMessage = null
        )
    }

    fun submitRequest(
        ownerId: String,
        walkerId: String
    ) {
        val state = _uiState.value

        if (state.isSubmitting || state.isSuccess) return

        val duration = state.durationMinutes.toIntOrNull()

        when {
            ownerId.isBlank() || walkerId.isBlank() -> {
                setError(
                    "No se pudo identificar al usuario o paseador."
                )
                return
            }

            state.selectedPetIds.isEmpty() -> {
                setError("Seleccioná al menos una mascota.")
                return
            }

            state.scheduledAt.isBlank() -> {
                setError("Ingresá la fecha y hora del paseo.")
                return
            }

            duration == null || duration <= 0 -> {
                setError("Ingresá una duración válida.")
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSubmitting = true,
                isSuccess = false,
                errorMessage = null
            )

            try {
                val request = WalkRequest(
                    id = UUID.randomUUID().toString(),
                    ownerId = ownerId,
                    walkerId = walkerId,
                    petIds = state.selectedPetIds.toList(),
                    scheduledAt = state.scheduledAt,
                    durationMinutes = duration,
                    status = RequestStatus.PENDING,
                    observations = state.observations.trim()
                )

                createWalkRequestUseCase(request)

                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    isSuccess = true,
                    errorMessage = null
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    isSuccess = false,
                    errorMessage = e.message
                        ?: "No se pudo enviar la solicitud."
                )
            }
        }
    }

    /**
     * Consume el estado de éxito antes de abandonar el formulario.
     * Evita que la modal vuelva a mostrarse al recuperar la pantalla.
     */
    fun consumeSuccess() {
        if (_uiState.value.isSuccess) {
            _uiState.value = _uiState.value.copy(
                isSuccess = false
            )
        }
    }

    private fun setError(message: String) {
        _uiState.value = _uiState.value.copy(
            errorMessage = message
        )
    }
}