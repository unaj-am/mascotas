package com.app.features.home.walker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.model.RequestStatus
import com.app.core.domain.model.UserRole
import com.app.core.domain.model.WalkStatus
import com.app.core.domain.usecase.GetUserByIdUseCase
import com.app.core.domain.usecase.GetWalkRequestsByWalkerUseCase
import com.app.core.domain.usecase.GetWalkerByIdUseCase
import com.app.core.domain.usecase.GetWalksByWalkerUseCase
import com.app.core.domain.usecase.UpdateWalkerAvailabilityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalkerHomeViewModel @Inject constructor(
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val getWalkerByIdUseCase: GetWalkerByIdUseCase,
    private val getWalkRequestsByWalkerUseCase: GetWalkRequestsByWalkerUseCase,
    private val getWalksByWalkerUseCase: GetWalksByWalkerUseCase,
    private val updateWalkerAvailabilityUseCase: UpdateWalkerAvailabilityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalkerHomeUiState())
    val uiState: StateFlow<WalkerHomeUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null
    private var homeLoadJob: Job? = null

    fun loadHome(
        userId: String,
        forceRefresh: Boolean = false
    ) {
        if (userId.isBlank()) {
            homeLoadJob?.cancel()
            currentUserId = null
            _uiState.value = WalkerHomeUiState(
                isLoading = false,
                errorMessage = "No se pudo identificar al paseador."
            )
            return
        }

        if (
            !forceRefresh &&
            currentUserId == userId &&
            !_uiState.value.isLoading &&
            _uiState.value.errorMessage == null
        ) {
            return
        }

        if (currentUserId != userId) {
            homeLoadJob?.cancel()
            _uiState.value = WalkerHomeUiState()
        }
        currentUserId = userId
        homeLoadJob?.cancel()

        homeLoadJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    availabilityMessage = null
                )
            }

            try {
                val user = getUserByIdUseCase(userId)
                    ?: throw IllegalStateException(
                        "No se encontró el usuario."
                    )

                require(user.role == UserRole.WALKER) {
                    "La cuenta no corresponde a un paseador."
                }

                if (currentUserId != userId) return@launch

                val (walker, requests, walks) = coroutineScope {
                    val walkerDeferred = async {
                        getWalkerByIdUseCase(userId)
                    }
                    val requestsDeferred = async {
                        getWalkRequestsByWalkerUseCase(userId)
                    }
                    val walksDeferred = async {
                        getWalksByWalkerUseCase(userId)
                    }
                    Triple(
                        walkerDeferred.await(),
                        requestsDeferred.await(),
                        walksDeferred.await()
                    )
                }

                if (currentUserId != userId) return@launch

                // Conservamos únicamente las solicitudes pendientes.
                val pendingRequests = requests.filter { request ->
                    request.status == RequestStatus.PENDING
                }

                _uiState.update { currentState ->
                    currentState.copy(
                        userName = user.name,
                        profileImage = user.profileImage,
                        available = walker?.available ?: false,
                        pendingRequests = pendingRequests.size,
                        pendingRequestItems = pendingRequests,
                        scheduledWalks = walks.count { walk ->
                            walk.status == WalkStatus.SCHEDULED
                        },
                        activeWalks = walks.count { walk ->
                            walk.status == WalkStatus.IN_PROGRESS
                        },
                        rating = walker?.rating,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                if (currentUserId == userId) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message
                                ?: "No se pudo cargar la información de inicio."
                        )
                    }
                }
            }
        }
    }

    fun onAvailabilityChange(newAvailable: Boolean) {
        val userId = currentUserId ?: run {
            _uiState.update {
                it.copy(
                    errorMessage =
                        "No se pudo identificar al paseador."
                )
            }
            return
        }

        val state = _uiState.value

        if (
            state.isLoading ||
            state.isUpdatingAvailability ||
            state.available == newAvailable
        ) {
            return
        }

        _uiState.update {
            it.copy(
                isUpdatingAvailability = true,
                errorMessage = null,
                availabilityMessage = null
            )
        }

        viewModelScope.launch {
            try {
                updateWalkerAvailabilityUseCase(
                    userId = userId,
                    available = newAvailable
                )

                if (currentUserId == userId) {
                    _uiState.update {
                        it.copy(
                            available = newAvailable,
                            isUpdatingAvailability = false,
                            availabilityMessage = if (newAvailable) {
                                "Ahora estás disponible para recibir solicitudes."
                            } else {
                                "Ya no estás disponible para nuevas solicitudes."
                            },
                            errorMessage = null
                        )
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                if (currentUserId == userId) {
                    _uiState.update {
                        it.copy(
                            isUpdatingAvailability = false,
                            errorMessage = error.message
                                ?: "No se pudo actualizar la disponibilidad."
                        )
                    }
                }
            }
        }
    }

    fun retryLoadHome() {
        val userId = currentUserId ?: return
        loadHome(userId, forceRefresh = true)
    }
}