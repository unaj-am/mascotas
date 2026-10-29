
package com.app.features.pets.form.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.ui.components.AppDropdownField

/**
 * Selector de comportamiento específico de mascotas.
 *
 * La lista de opciones permanece en features:pets.
 */
@Composable
fun PetBehaviorSelector(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    AppDropdownField(
        value = value,
        onValueChange = onValueChange,
        options = listOf(
            "Tranquilo",
            "Sociable",
            "Juguetón",
            "Tímido",
            "Nervioso"
        ),
        label = "Comportamiento",
        placeholder = "Seleccioná un comportamiento",
        enabled = enabled,
        modifier = modifier
    )
}
