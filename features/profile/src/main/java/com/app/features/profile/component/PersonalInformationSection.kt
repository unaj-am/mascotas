
package com.app.features.profile.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.app.core.ui.components.AppFormSection
import com.app.core.ui.components.AppTextField
import com.app.core.ui.theme.DimensUI

@Composable
fun PersonalInformationSection(
    name: String,
    lastName: String,
    email: String,
    phone: String,
    enabled: Boolean,
    onNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit
) {
    AppFormSection(
        title = "Datos personales"
    ) {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(
                DimensUI.SpaceMedium
            )
        ) {
            AppTextField(
                value = name,
                onValueChange = onNameChange,
                label = "Nombre",
                enabled = enabled
            )

            AppTextField(
                value = lastName,
                onValueChange = onLastNameChange,
                label = "Apellido",
                enabled = enabled
            )

            AppTextField(
                value = email,
                onValueChange = {},
                label = "Correo electrónico",
                readOnly = true
            )

            AppTextField(
                value = phone,
                onValueChange = onPhoneChange,
                label = "Teléfono",
                keyboardType = KeyboardType.Phone,
                enabled = enabled
            )
        }
    }
}
