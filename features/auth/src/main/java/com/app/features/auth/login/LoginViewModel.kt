package com.app.features.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.usecase.auth.SignInUseCase
import com.app.core.domain.usecase.auth.IsEmailVerifiedUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val isEmailVerifiedUseCase: IsEmailVerifiedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())

    val uiState: StateFlow<LoginUiState> =
        _uiState.asStateFlow()

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
                it.copy(
                    errorMessage = "Completá el correo y la contraseña"
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }

            try {
                val signInResult = signInUseCase(
                    email = email,
                    password = password
                )

                signInResult.fold(
                    onSuccess = {
                        isEmailVerifiedUseCase().fold(
                            onSuccess = { verified ->
                                if (verified) {
                                    _uiState.update {
                                        it.copy(
                                            isLoading = false,
                                            isSuccess = true,
                                            password = "",
                                            errorMessage = null
                                        )
                                    }
                                } else {
                                    _uiState.update {
                                        it.copy(
                                            isLoading = false,
                                            isSuccess = false,
                                            password = "",
                                            errorMessage = "Tu cuenta todavía no está verificada. Revisá tu correo electrónico."
                                        )
                                    }
                                }
                            },
                            onFailure = { error ->
                                if (error is CancellationException) throw error

                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        isSuccess = false,
                                        errorMessage = "No se pudo comprobar la verificación del correo. Intentá nuevamente."
                                    )
                                }
                            }
                        )
                    },
                    onFailure = { error ->
                        if (error is CancellationException) throw error

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "No se pudo iniciar sesión. Verificá tus datos."
                            )
                        }
                    }
                )
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = "Ocurrió un error al iniciar sesión."
                    )
                }
            }
        }
    }
}