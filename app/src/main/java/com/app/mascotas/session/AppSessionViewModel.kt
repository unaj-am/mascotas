
package com.app.mascotas.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.model.UserRole
import com.app.core.domain.repository.UserRepository
import com.app.core.domain.usecase.auth.GetCurrentUserIdUseCase
import com.app.core.domain.usecase.auth.IsEmailVerifiedUseCase
import com.app.core.domain.usecase.auth.ObserveAuthStateUseCase
import com.app.core.domain.usecase.auth.ResendEmailVerificationUseCase
import com.app.core.domain.usecase.auth.SignOutUseCase
import com.app.mascotas.navigation.AppSessionState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AppSessionViewModel @Inject constructor(
    private val observeAuthStateUseCase: ObserveAuthStateUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val isEmailVerifiedUseCase: IsEmailVerifiedUseCase,
    private val resendEmailVerificationUseCase: ResendEmailVerificationUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<AppSessionState>(AppSessionState.Loading)

    val uiState: StateFlow<AppSessionState> = _uiState.asStateFlow()

    private var validationJob: Job? = null

    init {
        viewModelScope.launch {
            observeAuthStateUseCase().collect { userId ->
                validationJob?.cancel()

                if (userId.isNullOrBlank()) {
                    _uiState.value = AppSessionState.SignedOut
                } else {
                    validationJob = launch {
                        validateSession(userId)
                    }
                }
            }
        }
    }

    private suspend fun validateSession(userId: String) {
        _uiState.value = AppSessionState.Loading

        try {
            val verificationResult = isEmailVerifiedUseCase()

            val verified = verificationResult.getOrElse { error ->
                if (error is CancellationException) throw error

                _uiState.value = AppSessionState.Error(
                    "No se pudo comprobar la verificación del correo. " +
                            "Revisá tu conexión e intentá nuevamente."
                )
                return
            }

            if (!verified) {
                _uiState.value = AppSessionState.NeedsVerification()
                return
            }

            val user = userRepository.getUserById(userId)

            if (user == null) {
                _uiState.value = AppSessionState.Error(
                    "No se encontró el perfil del usuario en Firestore. " +
                            "No se puede abrir el panel."
                )
                return
            }

            if (!user.active) {
                _uiState.value = AppSessionState.Error(
                    "Tu cuenta está inactiva. Contactá al administrador."
                )
                return
            }

            if (user.role != UserRole.OWNER &&
                user.role != UserRole.WALKER
            ) {
                _uiState.value = AppSessionState.Error(
                    "El rol del usuario no es válido."
                )
                return
            }

            _uiState.value = AppSessionState.Authenticated(
                userId = user.id,
                role = user.role
            )
        } catch (error: CancellationException) {
            throw error
        } catch (_: Exception) {
            _uiState.value = AppSessionState.Error(
                "No se pudo validar tu sesión. " +
                        "Revisá tu conexión e intentá nuevamente."
            )
        }
    }

    fun retrySessionValidation() {
        viewModelScope.launch {
            val userId = getCurrentUserIdUseCase()

            if (userId.isNullOrBlank()) {
                _uiState.value = AppSessionState.SignedOut
                return@launch
            }

            validationJob?.cancel()
            validationJob = launch {
                validateSession(userId)
            }
        }
    }

    fun resendVerificationEmail() {
        val currentState = _uiState.value

        if (currentState !is AppSessionState.NeedsVerification ||
            currentState.isLoading
        ) {
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(
                isLoading = true,
                message = null
            )

            try {
                resendEmailVerificationUseCase().fold(
                    onSuccess = {
                        _uiState.value =
                            AppSessionState.NeedsVerification(
                                message = "Se solicitó el envío del correo. " +
                                        "Revisá tu bandeja de entrada y spam."
                            )
                    },
                    onFailure = { error ->
                        if (error is CancellationException) throw error

                        _uiState.value =
                            AppSessionState.NeedsVerification(
                                message = "No se pudo reenviar el correo. " +
                                        "Intentá nuevamente."
                            )
                    }
                )
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                _uiState.value = AppSessionState.NeedsVerification(
                    message = "No se pudo reenviar el correo. " +
                            "Intentá nuevamente."
                )
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                signOutUseCase()
                _uiState.value = AppSessionState.SignedOut
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                _uiState.value = AppSessionState.Error(
                    "No se pudo cerrar la sesión. Intentá nuevamente."
                )
            }
        }
    }
}
