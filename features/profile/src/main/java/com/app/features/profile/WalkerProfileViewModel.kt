package com.app.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.model.UserRole
import com.app.core.domain.model.Walker
import com.app.core.domain.repository.UserRepository
import com.app.core.domain.usecase.GetWalkerByIdUseCase
import com.app.core.domain.usecase.SaveWalkerUseCase
import com.app.core.domain.usecase.UpdateUserUseCase
import com.app.core.domain.usecase.UpdateWalkerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.app.cloudinary.CloudinaryPhotoUploader
import android.net.Uri

@HiltViewModel
class WalkerProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val getWalkerByIdUseCase: GetWalkerByIdUseCase,
    private val saveWalkerUseCase: SaveWalkerUseCase,
    private val updateWalkerUseCase: UpdateWalkerUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val cloudinaryPhotoUploader: CloudinaryPhotoUploader
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalkerProfileUiState())
    val uiState: StateFlow<WalkerProfileUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null

    fun loadProfile(userId: String) {
        if (userId.isBlank()) {
            showError("No se pudo identificar al usuario.")
            return
        }

        // Evita volver a cargar un perfil que ya está cargado.
        if (
            currentUserId == userId &&
            !_uiState.value.isLoading &&
            _uiState.value.email.isNotBlank()
        ) {
            return
        }

        currentUserId = userId

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null
                )
            }

            try {
                val user = userRepository.getUserById(userId)
                    ?: throw IllegalStateException(
                        "No se encontró el usuario."
                    )

                if (user.role != UserRole.WALKER) {
                    throw IllegalStateException(
                        "El usuario no tiene un perfil de paseador."
                    )
                }

                val walker = getWalkerByIdUseCase(userId)

                _uiState.update { state ->
                    state.copy(
                        name = user.name,
                        lastName = user.lastName,
                        email = user.email,
                        phone = user.phone,
                        profileImage = user.profileImage,
                        description = walker?.description.orEmpty(),
                        experienceYears =
                            walker?.experienceYears?.toString() ?: "0",
                        serviceArea = walker?.serviceArea.orEmpty(),
                        pricePerWalk =
                            walker?.pricePerWalk?.toString() ?: "0",
                        maxPets = walker?.maxPets?.toString() ?: "1",
                        rating = walker?.rating,
                        isLoading = false,
                        isProfileCreated = walker != null,
                        errorMessage = null,
                        successMessage = null
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                showError(
                    e.message ?: "No se pudo cargar el perfil."
                )
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }

// ---------------------------------------------------------
    // Datos personales
    // ---------------------------------------------------------

    fun onNameChange(value: String) {
        _uiState.update {
            it.copy(
                name = value,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun onLastNameChange(value: String) {
        _uiState.update {
            it.copy(
                lastName = value,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun onPhoneChange(value: String) {
        _uiState.update {
            it.copy(
                phone = value,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    // ---------------------------------------------------------
    // Datos profesionales
    // ---------------------------------------------------------

    fun onDescriptionChange(value: String) {
        _uiState.update {
            it.copy(
                description = value,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun onExperienceYearsChange(value: String) {
        if (value.isEmpty() || value.toIntOrNull() != null) {
            _uiState.update {
                it.copy(
                    experienceYears = value,
                    errorMessage = null,
                    successMessage = null
                )
            }
        }
    }

    fun onServiceAreaChange(value: String) {
        _uiState.update {
            it.copy(
                serviceArea = value,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun onPricePerWalkChange(value: String) {
        if (value.isEmpty() || value.toDoubleOrNull() != null) {
            _uiState.update {
                it.copy(
                    pricePerWalk = value,
                    errorMessage = null,
                    successMessage = null
                )
            }
        }
    }

    fun onMaxPetsChange(value: String) {
        if (value.isEmpty() || value.toIntOrNull() != null) {
            _uiState.update {
                it.copy(
                    maxPets = value,
                    errorMessage = null,
                    successMessage = null
                )
            }
        }
    }

    // ---------------------------------------------------------
    // Guardar perfil
    // ---------------------------------------------------------

    fun saveProfile() {
        val userId = currentUserId

        if (userId.isNullOrBlank()) {
            showError("No se pudo identificar al usuario.")
            return
        }

        val state = _uiState.value

        if (state.isLoading || state.isSaving) {
            return
        }

        // Validaciones de datos personales.
        if (state.name.isBlank()) {
            showError("Ingresá tu nombre.")
            return
        }

        if (state.lastName.isBlank()) {
            showError("Ingresá tu apellido.")
            return
        }

        if (state.phone.isBlank()) {
            showError("Ingresá tu teléfono.")
            return
        }

        // Validaciones de datos profesionales.
        if (state.description.isBlank()) {
            showError("Ingresá una descripción profesional.")
            return
        }

        if (state.serviceArea.isBlank()) {
            showError("Ingresá tu zona de servicio.")
            return
        }

        val experienceYears = state.experienceYears.toIntOrNull()
        if (experienceYears == null || experienceYears < 0) {
            showError("Ingresá una cantidad válida de años de experiencia.")
            return
        }

        val pricePerWalk = state.pricePerWalk.toDoubleOrNull()
        if (pricePerWalk == null || pricePerWalk < 0) {
            showError("Ingresá un precio válido por paseo.")
            return
        }

        val maxPets = state.maxPets.toIntOrNull()
        if (maxPets == null || maxPets < 1) {
            showError("La cantidad máxima de mascotas debe ser al menos 1.")
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSaving = true,
                    errorMessage = null,
                    successMessage = null
                )
            }

            try {
                // Recupera el usuario actual para conservar sus otros datos.
                val user = userRepository.getUserById(userId)
                    ?: throw IllegalStateException(
                        "No se encontró el usuario."
                    )

                if (user.role != UserRole.WALKER) {
                    throw IllegalStateException(
                        "El usuario no tiene un perfil de paseador."
                    )
                }

                // Actualiza los datos personales del usuario.
                val updatedUser = user.copy(
                    name = state.name.trim(),
                    lastName = state.lastName.trim(),
                    phone = state.phone.trim()
                )

                updateUserUseCase(updatedUser)

                // Recupera la disponibilidad más reciente desde el repositorio.
                // No utiliza un valor guardado previamente en el estado de la pantalla.
                val existingWalker = getWalkerByIdUseCase(userId)

                // Construye el perfil profesional.
                val walker = Walker(
                    userId = userId,
                    description = state.description.trim(),
                    experienceYears = experienceYears,
                    serviceArea = state.serviceArea.trim(),
                    available = existingWalker?.available ?: false,
                    pricePerWalk = pricePerWalk,
                    maxPets = maxPets,
                    rating = state.rating ?: 0.0
                )

                // Guarda un perfil nuevo o actualiza el existente.
                if (existingWalker != null) {
                    updateWalkerUseCase(walker)
                } else {
                    saveWalkerUseCase(walker)
                }

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        isProfileCreated = true,
                        errorMessage = null,
                        successMessage = "Perfil actualizado correctamente."
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = e.message
                            ?: "No se pudo guardar el perfil.",
                        successMessage = null
                    )
                }
            }
        }
    }


    fun uploadProfilePhoto(uri: Uri) {
        val userId = currentUserId

        if (userId.isNullOrBlank()) {
            _uiState.update {
                it.copy(photoErrorMessage = "No se pudo identificar al usuario.")
            }
            return
        }

        if (_uiState.value.isLoading ||
            _uiState.value.isSaving ||
            _uiState.value.isUploadingPhoto
        ) {
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isUploadingPhoto = true,
                    photoErrorMessage = null
                )
            }

            try {
                val imageUrl = cloudinaryPhotoUploader.uploadPhoto(uri)

                userRepository.updateProfileImage(
                    userId = userId,
                    imageUrl = imageUrl
                )

                _uiState.update {
                    it.copy(
                        profileImage = imageUrl,
                        isUploadingPhoto = false,
                        photoErrorMessage = null
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isUploadingPhoto = false,
                        photoErrorMessage = e.message
                            ?: "No se pudo guardar la fotografía."
                    )
                }
            }
        }
    }
    // ---------------------------------------------------------
    // Reintentar carga
    // ---------------------------------------------------------
    fun retryLoadProfile() {
        val userId = currentUserId ?: return

        currentUserId = null
        loadProfile(userId)
    }

    private fun showError(message: String) {
        _uiState.update {
            it.copy(
                errorMessage = message,
                successMessage = null
            )
        }
    }
}