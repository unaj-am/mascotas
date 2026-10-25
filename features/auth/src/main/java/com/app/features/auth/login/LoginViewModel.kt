
package com.app.features.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.usecase.auth.SignInUseCase
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
    private val signInUseCase: SignInUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())

    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                errorMessage = null
            )
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
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            try {
                signInUseCase(
                    email = email,
                    password = password
                ).fold(
                    onSuccess = {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                password = "",
                                errorMessage = null
                            )
                        }
                    },
                    onFailure = { error ->
                        if (error is CancellationException) throw error

                        showError(
                            "No se pudo iniciar sesión. " +
                                    "Verificá el correo y la contraseña."
                        )
                    }
                )
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                showError("Ocurrió un error al iniciar sesión.")
            }
        }
    }
    fun resetForm() {
        _uiState.value = LoginUiState()
    }
    private fun showError(message: String) {
        _uiState.update {
            it.copy(
                isLoading = false,
                isSuccess = false,
                password = "",
                errorMessage = message
            )
        }
    }
}
