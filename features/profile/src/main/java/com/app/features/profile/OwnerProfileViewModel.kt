
package com.app.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.model.User
import com.app.core.domain.model.UserRole
import com.app.core.domain.usecase.GetUserByIdUseCase
import com.app.core.domain.usecase.UpdateUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.app.cloudinary.CloudinaryPhotoUploader
import com.app.core.domain.repository.UserRepository
import android.net.Uri


@HiltViewModel
class OwnerProfileViewModel @Inject constructor(
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val cloudinaryPhotoUploader: CloudinaryPhotoUploader,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OwnerProfileUiState())

    val uiState: StateFlow<OwnerProfileUiState> =
        _uiState.asStateFlow()

    private var currentUser: User? = null

    fun uploadProfilePhoto(uri: Uri) {
        val user = currentUser ?: run {
            _uiState.update {
                it.copy(
                    photoErrorMessage = "No se pudo identificar al usuario."
                )
            }
            return
        }

        if (
            _uiState.value.isLoading ||
            _uiState.value.isUploadingPhoto ||
            _uiState.value.isSaving
        ) return

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
                    userId = user.id,
                    imageUrl = imageUrl
                )
                currentUser = user.copy(
                    profileImage = imageUrl
                )

                _uiState.update {
                    it.copy(
                        profileImage = imageUrl,
                        isUploadingPhoto = false,
                        photoErrorMessage = null
                    )
                }

            } catch (error: CancellationException) {
                throw error

            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isUploadingPhoto = false,
                        photoErrorMessage = error.message
                            ?: "No se pudo guardar la fotografía."
                    )
                }
            }
        }
    }

    fun loadProfile(userId: String) {
        if (userId.isBlank()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "No se pudo identificar al usuario."
                )
            }
            return
        }

        if (
            _uiState.value.userId == userId &&
            !_uiState.value.isLoading &&
            currentUser != null
        ) {
            return
        }

        viewModelScope.launch {
            _uiState.value = OwnerProfileUiState(
                userId = userId,
                isLoading = true
            )

            try {
                val user = getUserByIdUseCase(userId)

                if (user == null) {
                    currentUser = null
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage =
                                "No se encontró el perfil del usuario."
                        )
                    }
                    return@launch
                }

                if (user.role != UserRole.OWNER) {
                    currentUser = null
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage =
                                "Esta cuenta no tiene el rol de dueño."
                        )
                    }
                    return@launch
                }

                currentUser = user

                _uiState.value = OwnerProfileUiState(
                    userId = user.id,
                    name = user.name,
                    lastName = user.lastName,
                    email = user.email,
                    phone = user.phone,
                    profileImage = user.profileImage,
                    isLoading = false
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

    fun saveProfile() {
        val originalUser = currentUser ?: run {
            _uiState.update {
                it.copy(
                    errorMessage =
                        "No se pudo identificar el usuario a modificar."
                )
            }
            return
        }

        val state = _uiState.value

        if (state.isLoading || state.isSaving) return

        when {
            state.name.isBlank() -> {
                showError("Ingresá tu nombre.")
                return
            }

            state.lastName.isBlank() -> {
                showError("Ingresá tu apellido.")
                return
            }

            state.phone.isBlank() -> {
                showError("Ingresá tu teléfono.")
                return
            }
        }

        val updatedUser = originalUser.copy(
            name = state.name.trim(),
            lastName = state.lastName.trim(),
            phone = state.phone.trim()
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
                updateUserUseCase(updatedUser)

                currentUser = updatedUser

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        successMessage =
                            "Los datos del perfil se guardaron correctamente."
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
        val userId = _uiState.value.userId
        currentUser = null
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
