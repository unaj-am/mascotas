package com.app.features.walks.owner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.usecase.GetUserByIdUseCase
import com.app.core.domain.usecase.GetWalkByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
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

    fun load(
        walkId: String,
        forceRefresh: Boolean = false
    ) {
        if (walkId.isBlank()) {
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

        currentWalkId = walkId

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            try {
                val walk = getWalkById(walkId)
                    ?: throw IllegalStateException(
                        "No se encontró el paseo solicitado."
                    )

                // Mostramos primero el paseo, sin esperar
                // a que se cargue el perfil del paseador.
                _uiState.value = OwnerWalkDetailUiState(
                    walk = walk,
                    isLoading = false
                )

                // El nombre se obtiene del usuario asociado
                // al walkerId almacenado en el paseo.
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

                _uiState.update {
                    if (it.walk?.id == walk.id) {
                        it.copy(walkerName = walkerName)
                    } else {
                        it
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
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

    fun refresh() {
        if (currentWalkId.isNotBlank()) {
            load(
                walkId = currentWalkId,
                forceRefresh = true
            )
        }
    }
}