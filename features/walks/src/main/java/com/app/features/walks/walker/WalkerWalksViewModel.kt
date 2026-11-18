
package com.app.features.walks.walker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.model.Walk
import com.app.core.domain.usecase.CompleteWalkUseCase
import com.app.core.domain.usecase.GetPetByIdUseCase
import com.app.core.domain.usecase.GetUserByIdUseCase
import com.app.core.domain.repository.WalkObservationRepository
import com.app.core.domain.usecase.StartWalkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class WalkerWalksViewModel @Inject constructor(
    private val walkObservationRepository: WalkObservationRepository,
    private val startWalk: StartWalkUseCase,
    private val completeWalk: CompleteWalkUseCase,
    private val getUserById: GetUserByIdUseCase,
    private val getPetById: GetPetByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalkerWalksUiState())
    val uiState = _uiState.asStateFlow()

    private var walkerId: String = ""
    private var loadJob: Job? = null

    fun load(
        walkerId: String,
        forceRefresh: Boolean = false
    ) {
        if (walkerId.isBlank()) {
            loadJob?.cancel()
            this.walkerId = ""

            _uiState.update {
                it.copy(
                    walks = emptyList(),
                    walkDetails = emptyMap(),
                    isLoading = false,
                    errorMessage = "No se pudo identificar al paseador."
                )
            }
            return
        }

        val walkerChanged = this.walkerId != walkerId
        this.walkerId = walkerId

        if (walkerChanged) {
            loadJob?.cancel()

            _uiState.update {
                it.copy(
                    walks = emptyList(),
                    walkDetails = emptyMap(),
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

            if (
                !_uiState.value.isLoading &&
                _uiState.value.errorMessage == null &&
                _uiState.value.walks.isNotEmpty()
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
                walkObservationRepository.observeWalksByWalker(walkerId)
                    .collectLatest { updatedWalks ->
                        if (this@WalkerWalksViewModel.walkerId != walkerId) return@collectLatest

                        val walks = updatedWalks.sortedBy { it.scheduledAt }
                        // Publicamos el estado antes de consultar los datos complementarios.
                        _uiState.update {
                            it.copy(
                                walks = walks,
                                isLoading = false,
                                errorMessage = null
                            )
                        }

                        val cachedDetails = _uiState.value.walkDetails
                        val details = coroutineScope {
                            walks.map { walk ->
                                async {
                                    walk.id to (cachedDetails[walk.id] ?: loadWalkDetails(walk))
                                }
                            }.awaitAll().toMap()
                        }

                        if (this@WalkerWalksViewModel.walkerId == walkerId) {
                            _uiState.update { it.copy(walkDetails = details) }
                        }
                    }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                if (this@WalkerWalksViewModel.walkerId == walkerId) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message
                                ?: "No se pudieron cargar los paseos."
                        )
                    }
                }
            }
        }
    }

    private suspend fun loadWalkDetails(
        walk: Walk
    ): WalkerWalkDetails = coroutineScope {

        val ownerNameDeferred = async {
            try {
                val owner = getUserById(walk.ownerId)

                owner?.let {
                    listOf(
                        it.name.trim(),
                        it.lastName.trim()
                    )
                        .filter { name -> name.isNotBlank() }
                        .joinToString(" ")
                        .takeIf { name -> name.isNotBlank() }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                null
            }
        }

        val petNamesDeferred = walk.petIds.map { petId ->
            async {
                try {
                    getPetById(petId)
                        ?.name
                        ?.trim()
                        ?.takeIf { name -> name.isNotBlank() }
                } catch (error: CancellationException) {
                    throw error
                } catch (_: Exception) {
                    null
                }
            }
        }

        val ownerName = ownerNameDeferred.await()
            ?: "Dueño no disponible"

        val petNames = petNamesDeferred
            .awaitAll()
            .filterNotNull()

        WalkerWalkDetails(
            ownerName = ownerName,
            petNames = petNames
        )
    }

    fun refresh() {
        if (walkerId.isBlank()) return
        load(walkerId, forceRefresh = true)
    }

    fun start(
        walkId: String,
        onStarted: () -> Unit = {}
    ) {
        perform(
            id = walkId,
            successMessage = "El paseo comenzó.",
            operation = {
                startWalk(walkId)
            },
            onSuccess = onStarted
        )
    }

    fun complete(
        walkId: String,
        onCompleted: () -> Unit = {}
    ) {
        perform(
            id = walkId,
            successMessage = "El paseo se marcó como finalizado.",
            operation = {
                completeWalk(walkId)
            },
            onSuccess = onCompleted
        )
    }

    private fun perform(
        id: String,
        successMessage: String,
        operation: suspend () -> Unit,
        onSuccess: () -> Unit
    ) {
        if (_uiState.value.processingId != null) return

        val currentWalkerId = walkerId
        _uiState.update {
            it.copy(
                processingId = id,
                operationMessage = null,
                operationError = null
            )
        }

        viewModelScope.launch {
            try {
                operation()

                if (walkerId == currentWalkerId) {
                    _uiState.update {
                        it.copy(
                            processingId = null,
                            operationMessage = successMessage
                        )
                    }
                }

                // Se ejecuta únicamente después de confirmar
                // que la operación de dominio terminó correctamente.
                onSuccess()

                // La observación mantiene actualizadas la agenda y el paseo activo.

            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                if (walkerId == currentWalkerId) {
                    _uiState.update {
                        it.copy(
                            processingId = null,
                            operationError = error.message
                                ?: "No se pudo actualizar el paseo."
                        )
                    }
                }
            }
        }
    }
}
