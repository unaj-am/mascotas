package com.app.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.model.RequestStatus
import com.app.core.domain.model.UserRole
import com.app.core.domain.model.WalkStatus
import com.app.core.domain.repository.UserRepository
import com.app.core.domain.usecase.GetWalkerByIdUseCase
import com.app.core.domain.usecase.GetWalkRequestsByWalkerUseCase
import com.app.core.domain.usecase.GetWalksByWalkerUseCase
import com.app.core.domain.usecase.UpdateWalkerAvailabilityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class WalkerHomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val getWalkerByIdUseCase: GetWalkerByIdUseCase,
    private val getWalkRequestsByWalkerUseCase: GetWalkRequestsByWalkerUseCase,
    private val getWalksByWalkerUseCase: GetWalksByWalkerUseCase,
    private val updateWalkerAvailabilityUseCase: UpdateWalkerAvailabilityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalkerHomeUiState())
    val uiState: StateFlow<WalkerHomeUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null

    fun loadHome(
        userId: String,
        forceRefresh: Boolean = false
    ) {
        if (userId.isBlank()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "No se pudo identificar al paseador."
                )
            }
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

        currentUserId = userId

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    availabilityMessage = null
                )
            }

            try {
                val user = userRepository.getUserById(userId)
                    ?: throw IllegalStateException(
                        "No se encontró el usuario."
                    )

                require(user.role == UserRole.WALKER) {
                    "La cuenta no corresponde a un paseador."
                }

                val walker = getWalkerByIdUseCase(userId)

                val requests =
                    getWalkRequestsByWalkerUseCase(userId)

                // Conservamos únicamente las solicitudes pendientes.
                val pendingRequests = requests.filter { request ->
                    request.status == RequestStatus.PENDING
                }

                val walks = getWalksByWalkerUseCase(userId)

                _uiState.update { currentState ->
                    currentState.copy(
                        userName = user.name,
                        available = walker?.available ?: false,

                        // Contador de solicitudes pendientes.
                        pendingRequests = pendingRequests.size,

                        // Lista completa para mostrar las solicitudes.
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

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isUpdatingAvailability = true,
                    errorMessage = null,
                    availabilityMessage = null
                )
            }

            try {
                updateWalkerAvailabilityUseCase(
                    userId = userId,
                    available = newAvailable
                )

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
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
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

    fun retryLoadHome() {
        val userId = currentUserId ?: return
        loadHome(userId, forceRefresh = true)
    }
}