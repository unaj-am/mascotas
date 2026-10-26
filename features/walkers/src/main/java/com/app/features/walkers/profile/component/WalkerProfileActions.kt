
package com.app.features.walkers.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.app.core.ui.components.DomainErrorMessage
import com.app.core.ui.components.PrimaryActionButton
import com.app.core.ui.theme.DimensUI

@Composable
fun WalkerProfileActions(
    isSaving: Boolean,
    isProfileCreated: Boolean,
    errorMessage: String?,
    successMessage: String?,
    onSave: () -> Unit,
    onSignOut: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SpaceSmall
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        errorMessage
            ?.takeIf { it.isNotBlank() }
            ?.let { message ->
                DomainErrorMessage(
                    message = message
                )
            }

        successMessage
            ?.takeIf { it.isNotBlank() }
            ?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

        PrimaryActionButton(
            text = if (isProfileCreated) {
                "Guardar cambios"
            } else {
                "Crear perfil de paseador"
            },
            onClick = onSave,
            enabled = !isSaving,
            isLoading = isSaving
        )

        TextButton(
            onClick = onSignOut,
            enabled = !isSaving
        ) {
            Text("Cerrar sesión")
        }
    }
}
