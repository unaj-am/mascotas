
package com.app.features.walkers.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.ui.components.AppTextField
import com.app.core.ui.theme.DimensUI

@Composable
fun WalkerPersonalInfoSection(
    name: String,
    lastName: String,
    email: String,
    phone: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SpaceMedium
        )
    ) {
        Text(
            text = "Datos personales",
            style = MaterialTheme.typography.titleLarge
        )

        AppTextField(
            value = name,
            onValueChange = {},
            label = "Nombre",
            readOnly = true
        )

        AppTextField(
            value = lastName,
            onValueChange = {},
            label = "Apellido",
            readOnly = true
        )

        AppTextField(
            value = email,
            onValueChange = {},
            label = "Correo electrónico",
            readOnly = true
        )

        AppTextField(
            value = phone,
            onValueChange = {},
            label = "Teléfono",
            readOnly = true
        )
    }
}
