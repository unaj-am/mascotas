package com.app.features.auth.verification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.core.ui.components.AppCard
import com.app.core.ui.components.DomainErrorMessage
import com.app.core.ui.components.PrimaryActionButton
import com.app.core.ui.components.SectionHeader
import com.app.core.ui.theme.MascotasThemeUI

@Composable
fun EmailVerificationScreen(
    isLoading: Boolean,
    message: String?,
    onCheckVerification: () -> Unit,
    onResendEmail: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppCard(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SectionHeader(
                    title = "Verificá tu correo",
                    subtitle = "Un paso más para comenzar a usar Mascotas."
                )

                Text(
                    text = "Para continuar, abrí el mensaje que te enviamos y verificá tu dirección de correo electrónico. Si ya lo hiciste, volvé a comprobar el estado.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                message?.takeIf { it.isNotBlank() }?.let {
                    DomainErrorMessage(
                        message = it,
                        modifier = Modifier.fillMaxWidth()
                    )
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
    }
}

@Preview(showBackground = true)
@Composable
private fun EmailVerificationPreview() {
    MascotasThemeUI {
        EmailVerificationScreen(
            isLoading = false,
            message = null,
            onCheckVerification = {},
            onResendEmail = {},
            onSignOut = {}
        )
    }
}

@Preview(
    name = "Verificación en ventana amplia",
    showBackground = true,
    widthDp = 840,
    heightDp = 1200
)
@Composable
private fun EmailVerificationWidePreview() {
    EmailVerificationPreview()
}
