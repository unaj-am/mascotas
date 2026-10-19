package com.app.features.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.usecase.auth.SendEmailVerificationUseCase
import com.app.core.domain.usecase.auth.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.app.core.domain.model.UserRole
import com.app.core.domain.model.User
import com.app.core.domain.repository.UserRepository
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val sendEmailVerificationUseCase: SendEmailVerificationUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())

    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.update {
            it.copy(name = name, errorMessage = null)
        }
    }

    fun onLastNameChange(lastName: String) {
        _uiState.update {
            it.copy(lastName = lastName, errorMessage = null)
        }
    }

    fun onPhoneChange(phone: String) {
        _uiState.update {
            it.copy(phone = phone, errorMessage = null)
        }
    }

    fun onRoleChange(role: UserRole) {
        _uiState.update {
            it.copy(role = role, errorMessage = null)
        }
    }

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(email = email, errorMessage = null)
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                errorMessage = null
            )
        }
    }

    fun onConfirmPasswordChange(password: String) {
        _uiState.update {
            it.copy(
                confirmPassword = password,
                errorMessage = null
            )
        }
    }

    fun register() {
        _uiState.update {
            it.copy(
                hasAttemptedSubmit = true,
                errorMessage = null
            )
        }

        val state = _uiState.value

        if (state.isLoading || state.isSuccess) return

        // Los errores se calculan desde RegisterUiState.
        if (state.hasValidationErrors) return

        val email = state.email.trim()
        val password = state.password
        val confirmPassword = state.confirmPassword

        when {
            state.name.isBlank() -> {
                updateError("Completá el nombre")
                return
            }

            state.lastName.isBlank() -> {
                updateError("Completá el apellido")
                return
            }

            email.isBlank() -> {
                updateError("Completá el correo electrónico")
                return
            }

            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                updateError("Ingresá un correo electrónico válido")
                return
            }

            state.phone.isBlank() -> {
                updateError("Completá el teléfono")
                return
            }

            password.isBlank() -> {
                updateError("Completá la contraseña")
                return
            }

            password.length < 6 -> {
                updateError("La contraseña debe tener al menos 6 caracteres")
                return
            }

            confirmPassword.isBlank() -> {
                updateError("Confirmá la contraseña")
                return
            }

            password != confirmPassword -> {
                updateError("Las contraseñas no coinciden")
                return
            }
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null
                )
            }

            try {
                val signUpResult = signUpUseCase(
                    email = email,
                    password = password
                )

                signUpResult.fold(
                    onSuccess = { userId ->
                        try {
                            val user = User(
                                id = userId,
                                name = state.name.trim(),
                                lastName = state.lastName.trim(),
                                email = email,
                                phone = state.phone.trim(),
                                role = state.role,
                                active = true
                            )

                            userRepository.saveUser(user)

                            sendEmailVerificationUseCase().fold(
                                onSuccess = {
                                    _uiState.update { current ->
                                        current.copy(
                                            isLoading = false,
                                            isSuccess = true,
                                            password = "",
                                            confirmPassword = "",
                                            successMessage =
                                                "Cuenta creada. Revisá tu correo para verificarla."
                                        )
                                    }
                                },
                                onFailure = { error ->
                                    if (error is CancellationException) throw error

                                    _uiState.update { current ->
                                        current.copy(
                                            isLoading = false,
                                            isSuccess = true,
                                            password = "",
                                            confirmPassword = "",
                                            successMessage =
                                                "La cuenta y el perfil fueron creados, pero no se pudo enviar el correo de verificación."
                                        )
                                    }
                                }
                            )
                        } catch (error: CancellationException) {
                            throw error
                        } catch (error: Exception) {
                            _uiState.update { current ->
                                current.copy(
                                    isLoading = false,
                                    errorMessage =
                                        "La cuenta se creó, pero no se pudo guardar el perfil. Contactá al soporte antes de intentar registrarte nuevamente."
                                )
                            }
                        }
                    },
                    onFailure = { error ->
                        if (error is CancellationException) throw error

                        updateError(
                            "No se pudo crear la cuenta. Verificá los datos o intentá con otro correo."
                        )
                    }
                )
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage =
                            "Ocurrió un error al registrarte. Intentá nuevamente."
                    )
                }
            }
        }
    }

    private fun updateError(message: String) {
        _uiState.update {
            it.copy(errorMessage = message)
        }
    }
}