
package com.app.features.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.model.UserRole
import com.app.core.domain.repository.UserRepository
import com.app.core.domain.usecase.auth.SignInUseCase
import com.app.core.domain.usecase.auth.IsEmailVerifiedUseCase
import com.app.core.domain.usecase.auth.GetCurrentUserIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val isEmailVerifiedUseCase: IsEmailVerifiedUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(email = email, errorMessage = null)
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update {
            it.copy(password = password, errorMessage = null)
        }
    }

    fun signIn() {
        val currentState = _uiState.value

        if (currentState.isLoading || currentState.isSuccess) return

        val email = currentState.email.trim()
        val password = currentState.password

        if (email.isBlank() || password.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Completá el correo y la contraseña")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    userId = null,
                    userRole = null
                )
            }

            try {
                val signInResult = signInUseCase(email, password)

                signInResult.fold(
                    onSuccess = {
                        val verificationResult = isEmailVerifiedUseCase()

                        verificationResult.fold(
                            onSuccess = { verified ->
                                if (!verified) {
                                    showError(
                                        "Tu cuenta todavía no está verificada. Revisá tu correo electrónico."
                                    )
                                } else {
                                    loadUserProfile()
                                }
                            },
                            onFailure = { error ->
                                if (error is CancellationException) throw error
                                showError(
                                    "No se pudo comprobar la verificación del correo. Intentá nuevamente."
                                )
                            }
                        )
                    },
                    onFailure = { error ->
                        if (error is CancellationException) throw error
                        showError(
                            "No se pudo iniciar sesión. Verificá tus datos."
                        )
                    }
                )
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                showError("Ocurrió un error al iniciar sesión.")
            }
        }
    }

    private suspend fun loadUserProfile() {
        val userId = getCurrentUserIdUseCase()

        if (userId.isNullOrBlank()) {
            showError("No se pudo identificar al usuario autenticado.")
            return
        }

        val user = userRepository.getUserById(userId)

        if (user == null) {
            showError(
                "No se encontró el perfil del usuario en Firestore."
            )
            return
        }

        if (!user.active) {
            showError("Tu cuenta está inactiva. Contactá al administrador.")
            return
        }

        if (user.role !in listOf(UserRole.OWNER, UserRole.WALKER)) {
            showError("El rol del usuario no es válido.")
            return
        }

        _uiState.update {
            it.copy(
                isLoading = false,
                isSuccess = true,
                password = "",
                userId = user.id,
                userRole = user.role,
                errorMessage = null
            )
        }
    }

    private fun showError(message: String) {
        _uiState.update {
            it.copy(
                isLoading = false,
                isSuccess = false,
                userId = null,
                userRole = null,
                password = "",
                errorMessage = message
            )
        }
    }
}
