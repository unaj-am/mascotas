package com.app.features.profile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.model.UserRole
import com.app.core.domain.model.Walker
import com.app.core.domain.repository.UserRepository
import com.app.core.domain.usecase.GetWalkerByIdUseCase
import com.app.core.domain.usecase.SaveWalkerUseCase
import com.app.core.domain.usecase.UpdateWalkerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class WalkerProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val getWalkerByIdUseCase: GetWalkerByIdUseCase,
    private val saveWalkerUseCase: SaveWalkerUseCase,
    private val updateWalkerUseCase: UpdateWalkerUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalkerProfileUiState())

    val uiState: StateFlow<com.app.features.profile.WalkerProfileUiState> =
        _uiState.asStateFlow()

    private var currentUserId: String? = null

    fun loadProfile(userId: String) {
        if (userId.isBlank()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "No se recibió el identificador del usuario."
                )
            }
            return
        }

        if (currentUserId == userId && _uiState.value.isLoading) {
            return
        }

        currentUserId = userId

        viewModelScope.launch {
            _uiState.value = WalkerProfileUiState(isLoading = true)

            try {
                val user = userRepository.getUserById(userId)

                if (user == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage =
                                "No se encontró el usuario en la base de datos."
                        )
                    }
                    return@launch
                }

                if (user.role != UserRole.WALKER) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage =
                                "Esta cuenta no tiene el rol de paseador."
                        )
                    }
                    return@launch
                }

                val walker = getWalkerByIdUseCase(userId)

                _uiState.value = WalkerProfileUiState(
                    name = user.name,
                    lastName = user.lastName,
                    email = user.email,
                    phone = user.phone,
                    description = walker?.description.orEmpty(),
                    experienceYears =
                        walker?.experienceYears?.toString() ?: "0",
                    serviceArea = walker?.serviceArea.orEmpty(),
                    available = walker?.available ?: false,
                    pricePerWalk =
                        walker?.pricePerWalk?.toString() ?: "0",
                    maxPets = walker?.maxPets?.toString() ?: "1",
                    rating = walker?.rating,
                    isLoading = false,
                    isProfileCreated = walker != null
                )
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage =
                            "No se pudo cargar el perfil. " +
                                    "Comprobá tu conexión e intentá nuevamente."
                    )
                }
            }
        }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update {
            it.copy(description = value, errorMessage = null)
        }
    }

    fun onExperienceYearsChange(value: String) {
        if (value.isEmpty() || value.all(Char::isDigit)) {
            _uiState.update {
                it.copy(experienceYears = value, errorMessage = null)
            }
        }
    }

    fun onServiceAreaChange(value: String) {
        _uiState.update {
            it.copy(serviceArea = value, errorMessage = null)
        }
    }

    fun onAvailableChange(value: Boolean) {
        _uiState.update {
            it.copy(available = value, errorMessage = null)
        }
    }

    fun onPricePerWalkChange(value: String) {
        if (value.isEmpty() || value.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) {
            _uiState.update {
                it.copy(pricePerWalk = value, errorMessage = null)
            }
        }
    }

    fun onMaxPetsChange(value: String) {
        if (value.isEmpty() || value.all(Char::isDigit)) {
            _uiState.update {
                it.copy(maxPets = value, errorMessage = null)
            }
        }
    }

    fun saveProfile() {
        val userId = currentUserId

        if (userId.isNullOrBlank()) {
            _uiState.update {
                it.copy(
                    errorMessage = "No se pudo identificar al usuario."
                )
            }
            return
        }

        val state = _uiState.value

        if (state.isLoading || state.isSaving) return

        val experienceYears = state.experienceYears.toIntOrNull()
        val pricePerWalk = state.pricePerWalk.toDoubleOrNull()
        val maxPets = state.maxPets.toIntOrNull()

        when {
            state.description.isBlank() -> {
                showError("Completá la descripción del paseador.")
                return
            }

            state.serviceArea.isBlank() -> {
                showError("Completá la zona de servicio.")
                return
            }

            experienceYears == null || experienceYears < 0 -> {
                showError("Ingresá una cantidad válida de años de experiencia.")
                return
            }

            pricePerWalk == null || !pricePerWalk.isFinite() ||
                    pricePerWalk < 0 -> {
                showError("Ingresá un precio por paseo válido.")
                return
            }

            maxPets == null || maxPets <= 0 -> {
                showError("La cantidad máxima de mascotas debe ser mayor que cero.")
                return
            }
        }

        val walker = Walker(
            userId = userId,
            description = state.description.trim(),
            experienceYears = experienceYears,
            serviceArea = state.serviceArea.trim(),
            available = state.available,
            pricePerWalk = pricePerWalk,
            maxPets = maxPets,
            rating = state.rating
        )

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSaving = true,
                    errorMessage = null,
                    successMessage = null
                )
            }

            try {
                if (_uiState.value.isProfileCreated) {
                    updateWalkerUseCase(walker)
                } else {
                    saveWalkerUseCase(walker)
                }

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        isProfileCreated = true,
                        successMessage = "El perfil se guardó correctamente."
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = error.message
                            ?: "No se pudo guardar el perfil."
                    )
                }
            }
        }
    }

    fun retryLoadProfile() {
        currentUserId?.let(::loadProfile)
    }

    private fun showError(message: String) {
        _uiState.update {
            it.copy(errorMessage = message)
        }
    }
}
