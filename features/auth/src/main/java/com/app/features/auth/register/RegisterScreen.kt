
package com.app.features.auth.register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.core.domain.model.UserRole
import com.app.core.ui.components.AppTextField
import com.app.core.ui.components.DomainErrorMessage
import com.app.core.ui.components.PrimaryActionButton
import com.app.core.ui.components.SuccessDialog
import com.app.core.ui.theme.DimensUI

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showSuccessDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            showSuccessDialog = true
        }
    }

    if (showSuccessDialog) {
        SuccessDialog(
            title = "Cuenta creada",
            message = uiState.successMessage
                ?: "Tu cuenta fue creada. Revisá tu correo " +
                "y verificá tu email antes de iniciar sesión.",
            confirmText = "Ir al inicio de sesión",
            onConfirm = {
                showSuccessDialog = false
                onRegisterSuccess()
            },
            onDismiss = {
                showSuccessDialog = false
            }
        )
    }

    val fieldsEnabled = !uiState.isLoading && !uiState.isSuccess

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = DimensUI.ScreenHorizontalPadding,
                vertical = DimensUI.ScreenVerticalPadding
            ),
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SpaceMedium
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Mascotas",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Crear cuenta",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        AppTextField(
            value = uiState.name,
            onValueChange = viewModel::onNameChange,
            label = "Nombre",
            enabled = fieldsEnabled,
            isError = uiState.nameError != null,
            errorMessage = uiState.nameError
        )

        AppTextField(
            value = uiState.lastName,
            onValueChange = viewModel::onLastNameChange,
            label = "Apellido",
            enabled = fieldsEnabled,
            isError = uiState.lastNameError != null,
            errorMessage = uiState.lastNameError
        )

        AppTextField(
            value = uiState.email,
            onValueChange = viewModel::onEmailChange,
            label = "Correo electrónico",
            keyboardType = KeyboardType.Email,
            enabled = fieldsEnabled,
            isError = uiState.emailError != null,
            errorMessage = uiState.emailError
        )

        AppTextField(
            value = uiState.phone,
            onValueChange = viewModel::onPhoneChange,
            label = "Teléfono",
            keyboardType = KeyboardType.Phone,
            enabled = fieldsEnabled,
            isError = uiState.phoneError != null,
            errorMessage = uiState.phoneError
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(
                DimensUI.SpaceSmall
            )
        ) {
            Text(
                text = "Tipo de cuenta",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            listOf(
                UserRole.OWNER,
                UserRole.WALKER
            ).forEach { role ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = fieldsEnabled) {
                            viewModel.onRoleChange(role)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = uiState.role == role,
                        onClick = null,
                        enabled = fieldsEnabled
                    )

                    Text(
                        text = when (role) {
                            UserRole.OWNER -> "Dueño de mascota"
                            UserRole.WALKER -> "Paseador"
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        AppTextField(
            value = uiState.password,
            onValueChange = viewModel::onPasswordChange,
            label = "Contraseña",
            keyboardType = KeyboardType.Password,
            visualTransformation = PasswordVisualTransformation(),
            enabled = fieldsEnabled,
            isError = uiState.passwordError != null,
            errorMessage = uiState.passwordError
        )

        AppTextField(
            value = uiState.confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChange,
            label = "Confirmar contraseña",
            keyboardType = KeyboardType.Password,
            visualTransformation = PasswordVisualTransformation(),
            enabled = fieldsEnabled,
            isError = uiState.confirmPasswordError != null,
            errorMessage = uiState.confirmPasswordError
        )

        uiState.errorMessage?.let { message ->
            DomainErrorMessage(
                message = message
            )
        }

        PrimaryActionButton(
            text = "Registrarme",
            onClick = viewModel::register,
            enabled = !uiState.isSuccess,
            isLoading = uiState.isLoading
        )

        TextButton(
            onClick = onNavigateToLogin,
            enabled = !uiState.isLoading && !uiState.isSuccess
        ) {
            Text(
                text = "Ya tengo una cuenta"
            )
        }
    }
}
