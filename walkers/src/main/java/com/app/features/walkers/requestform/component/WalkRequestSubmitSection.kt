
package com.app.features.walkers.requestform.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.ui.components.DomainErrorMessage
import com.app.core.ui.components.PrimaryActionButton
import com.app.core.ui.theme.DimensUI

@Composable
fun WalkRequestSubmitSection(
    errorMessage: String?,
    hasSelectedPets: Boolean,
    isSubmitting: Boolean,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SpaceMedium
        )
    ) {
        errorMessage?.let { message ->
            DomainErrorMessage(
                message = message,
                modifier = Modifier.fillMaxWidth()
            )
        }

        PrimaryActionButton(
            text = "Enviar solicitud",
            onClick = onSubmit,
            enabled = hasSelectedPets && !isSubmitting,
            isLoading = isSubmitting,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
