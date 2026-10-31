
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
    private var resendJob: Job? = null
    private var signOutJob: Job? = null

    private var operationId = 0L

    init {
        viewModelScope.launch {
            observeAuthStateUseCase().collect { userId ->
                if (userId.isNullOrBlank()) {
                    invalidateOperations()
                    _uiState.value = AppSessionState.SignedOut
                } else {
                    startSessionValidation(userId)
                }
            }
        }
    }

    private fun invalidateOperations() {
        operationId++

        validationJob?.cancel()
        validationJob = null

        resendJob?.cancel()
        resendJob = null

        signOutJob?.cancel()
        signOutJob = null
    }

    private fun startSessionValidation(userId: String) {
        operationId++
        val currentOperation = operationId

        validationJob?.cancel()
        resendJob?.cancel()
        signOutJob?.cancel()

        resendJob = null
        signOutJob = null

        _uiState.value = AppSessionState.Loading

        validationJob = viewModelScope.launch {
            validateSession(
                userId = userId,
                currentOperation = currentOperation
            )
        }
    }

    private suspend fun validateSession(
        userId: String,
        currentOperation: Long
    ) {
        try {
            val verificationResult = isEmailVerifiedUseCase()

            if (!isCurrentOperation(currentOperation)) return

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

            if (!isCurrentOperation(currentOperation)) return

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

            if (
                user.role != UserRole.OWNER &&
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
            if (isCurrentOperation(currentOperation)) {
                _uiState.value = AppSessionState.Error(
                    "No se pudo validar tu sesión. " +
                            "Revisá tu conexión e intentá nuevamente."
                )
            }
        }
    }

    fun retrySessionValidation() {
        if (signOutJob?.isActive == true) return

        operationId++
        val currentOperation = operationId

        validationJob?.cancel()
        resendJob?.cancel()

        validationJob = viewModelScope.launch {
            try {
                _uiState.value = AppSessionState.Loading

                val userId = getCurrentUserIdUseCase()

                if (!isCurrentOperation(currentOperation)) return@launch

                if (userId.isNullOrBlank()) {
                    _uiState.value = AppSessionState.SignedOut
                    return@launch
                }

                validateSession(
                    userId = userId,
                    currentOperation = currentOperation
                )
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                if (isCurrentOperation(currentOperation)) {
                    _uiState.value = AppSessionState.Error(
                        "No se pudo validar tu sesión. " +
                                "Revisá tu conexión e intentá nuevamente."
                    )
                }
            }
        }
    }

    fun resendVerificationEmail() {
        val currentState = _uiState.value

        if (
            currentState !is AppSessionState.NeedsVerification ||
            currentState.isLoading ||
            resendJob?.isActive == true ||
            signOutJob?.isActive == true
        ) {
            return
        }

        val currentOperation = operationId

        _uiState.value = currentState.copy(
            isLoading = true,
            message = null
        )

        resendJob = viewModelScope.launch {
            try {
                resendEmailVerificationUseCase().fold(
                    onSuccess = {
                        if (isCurrentOperation(currentOperation)) {
                            _uiState.value =
                                AppSessionState.NeedsVerification(
                                    message = "Se solicitó el envío del correo. " +
                                            "Revisá tu bandeja de entrada y spam."
                                )
                        }
                    },
                    onFailure = { error ->
                        if (error is CancellationException) throw error

                        if (isCurrentOperation(currentOperation)) {
                            _uiState.value =
                                AppSessionState.NeedsVerification(
                                    message = "No se pudo reenviar el correo. " +
                                            "Intentá nuevamente."
                                )
                        }
                    }
                )
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                if (isCurrentOperation(currentOperation)) {
                    _uiState.value = AppSessionState.NeedsVerification(
                        message = "No se pudo reenviar el correo. " +
                                "Intentá nuevamente."
                    )
                }
            }
        }
    }

    fun signOut() {
        if (signOutJob?.isActive == true) return

        operationId++
        val currentOperation = operationId

        validationJob?.cancel()
        resendJob?.cancel()

        validationJob = null
        resendJob = null

        _uiState.value = AppSessionState.Loading

        signOutJob = viewModelScope.launch {
            try {
                signOutUseCase()

                if (isCurrentOperation(currentOperation)) {
                    _uiState.value = AppSessionState.SignedOut
                }
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                if (isCurrentOperation(currentOperation)) {
                    _uiState.value = AppSessionState.Error(
                        "No se pudo cerrar la sesión. Intentá nuevamente."
                    )
                }
            }
        }
    }

    private fun isCurrentOperation(id: Long): Boolean {
        return operationId == id
    }
}
