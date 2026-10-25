
package com.app.features.auth.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.core.ui.components.AppTextField
import com.app.core.ui.components.DomainErrorMessage
import com.app.core.ui.components.PrimaryActionButton
import com.app.core.ui.theme.DimensUI

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.resetForm()
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onLoginSuccess()
        }
    }

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
            DimensUI.SpaceLarge,
            Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Mascotas",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        AppTextField(
            value = uiState.email,
            onValueChange = viewModel::onEmailChange,
            label = "Correo electrónico",
            placeholder = "ejemplo@correo.com",
            keyboardType = KeyboardType.Email,
            enabled = !uiState.isLoading && !uiState.isSuccess
        )

        AppTextField(
            value = uiState.password,
            onValueChange = viewModel::onPasswordChange,
            label = "Contraseña",
            keyboardType = KeyboardType.Password,
            visualTransformation = PasswordVisualTransformation(),
            enabled = !uiState.isLoading && !uiState.isSuccess
        )

        uiState.errorMessage?.let { message ->
            DomainErrorMessage(message = message)
        }

        PrimaryActionButton(
            text = "Ingresar",
            onClick = viewModel::signIn,
            enabled = !uiState.isSuccess,
            isLoading = uiState.isLoading
        )

        TextButton(
            onClick = onNavigateToRegister,
            enabled = !uiState.isLoading && !uiState.isSuccess
        ) {
            Text("¿No tenés cuenta? Registrate")
        }
    }
}
