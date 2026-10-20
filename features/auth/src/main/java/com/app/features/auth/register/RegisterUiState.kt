package com.app.features.auth.register

import android.util.Patterns
import com.app.core.domain.model.UserRole

data class RegisterUiState(
    val name: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val role: UserRole = UserRole.OWNER,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val hasAttemptedSubmit: Boolean = false
) {
    val nameError: String?
        get() = if (hasAttemptedSubmit && name.isBlank()) {
            "Completá el nombre"
        } else {
            null
        }

    val lastNameError: String?
        get() = if (hasAttemptedSubmit && lastName.isBlank()) {
            "Completá el apellido"
        } else {
            null
        }

    val emailError: String?
        get() = when {
            email.isBlank() && hasAttemptedSubmit ->
                "Completá el correo electrónico"

            email.isNotBlank() &&
                    !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() ->
                "Ingresá un correo electrónico válido"

            else -> null
        }

    val phoneError: String?
        get() {
            val digits = phone.filter { it.isDigit() }

            return when {
                phone.isBlank() && hasAttemptedSubmit ->
                    "Completá el teléfono"

                phone.isNotBlank() && digits.length !in 8..15 ->
                    "Ingresá un teléfono válido (entre 8 y 15 dígitos)"

                else -> null
            }
        }

    val passwordError: String?
        get() = when {
            password.isBlank() && hasAttemptedSubmit ->
                "Completá la contraseña"

            password.isNotBlank() && password.length < 6 ->
                "La contraseña debe tener al menos 6 caracteres"

            else -> null
        }

    val confirmPasswordError: String?
        get() = when {
            confirmPassword.isBlank() && hasAttemptedSubmit ->
                "Confirmá la contraseña"

            confirmPassword.isNotBlank() && password != confirmPassword ->
                "Las contraseñas no coinciden"

            else -> null
        }

    val hasValidationErrors: Boolean
        get() = nameError != null ||
                lastNameError != null ||
                emailError != null ||
                phoneError != null ||
                passwordError != null ||
                confirmPasswordError != null
}