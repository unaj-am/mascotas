
package com.app.features.walks.owner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.usecase.GetUserByIdUseCase
import com.app.core.domain.usecase.GetWalkByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class OwnerWalkDetailViewModel @Inject constructor(
    private val getWalkById: GetWalkByIdUseCase,
    private val getUserById: GetUserByIdUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(OwnerWalkDetailUiState())

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

            _uiState.value = OwnerWalkDetailUiState(
                isLoading = false,
                errorMessage =
                    "No se recibió el identificador del paseo."
            )
            return
        }

        if (
            !forceRefresh &&
            currentWalkId == walkId &&
            _uiState.value.walk?.id == walkId &&
            _uiState.value.errorMessage == null
        ) {
            return
        }

        val walkChanged = currentWalkId != walkId
        currentWalkId = walkId

        loadJob?.cancel()

        if (walkChanged) {
            _uiState.value = OwnerWalkDetailUiState(
                isLoading = true
            )
        } else {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }
        }

        loadJob = viewModelScope.launch {
            try {
                val walk = getWalkById(walkId)
                    ?: throw IllegalStateException(
                        "No se encontró el paseo solicitado."
                    )

                // Evita aplicar el resultado de una navegación anterior.
                if (currentWalkId != walkId) return@launch

                // Mostramos primero el paseo, sin esperar
                // a que se cargue el perfil del paseador.
                _uiState.value = OwnerWalkDetailUiState(
                    walk = walk,
                    isLoading = false
                )

                val walkerName = try {
                    val user = getUserById(walk.walkerId)

                    listOf(
                        user?.name.orEmpty().trim(),
                        user?.lastName.orEmpty().trim()
                    )
                        .filter { it.isNotBlank() }
                        .joinToString(" ")
                        .takeIf { it.isNotBlank() }
                } catch (error: CancellationException) {
                    throw error
                } catch (_: Exception) {
                    null
                }

                if (currentWalkId != walkId) return@launch

                _uiState.update { state ->
                    if (state.walk?.id == walkId) {
                        state.copy(walkerName = walkerName)
                    } else {
                        state
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                if (currentWalkId == walkId) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message
                                ?: "No se pudo cargar el detalle del paseo."
                        )
                    }
                }
            }
        }
    }

    fun refresh() {
        if (currentWalkId.isNotBlank()) {
            load(
                walkId = currentWalkId,
                forceRefresh = true
            )
        }
    }
}
