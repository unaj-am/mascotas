
package com.app.features.auth.verification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.core.ui.components.DomainErrorMessage
import com.app.core.ui.components.PrimaryActionButton

@Composable
fun EmailVerificationScreen(
    isLoading: Boolean,
    message: String?,
    onCheckVerification: () -> Unit,
    onResendEmail: () -> Unit,
    onSignOut: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(
            16.dp,
            Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Verificá tu correo",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Para continuar, verificá tu dirección de correo " +
                    "desde el mensaje que te enviamos. Si ya lo hiciste, " +
                    "volvé a comprobar el estado."
        )

        if (message != null) {
            DomainErrorMessage(message = message)
        }

        PrimaryActionButton(
            text = "Ya verifiqué mi correo",
            onClick = onCheckVerification,
            enabled = !isLoading,
            isLoading = isLoading
        )

        TextButton(
            onClick = onResendEmail,
            enabled = !isLoading
        ) {
            Text("Reenviar correo de verificación")
        }

        TextButton(
            onClick = onSignOut,
            enabled = !isLoading
        ) {
            Text("Cerrar sesión")
        }
    }
}
