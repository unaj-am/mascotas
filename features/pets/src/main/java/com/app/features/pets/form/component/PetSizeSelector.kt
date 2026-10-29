
package com.app.features.pets.form.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.ui.components.AppDropdownField

/**
 * Selector de tamaño específico del formulario de mascotas.
 *
 * Las opciones pertenecen a la funcionalidad pets;
 * el aspecto visual se delega a core:ui.
 */
@Composable
fun PetSizeSelector(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    AppDropdownField(
        value = value,
        onValueChange = onValueChange,
        options = listOf(
            "Pequeño",
            "Mediano",
            "Grande"
        ),
        label = "Tamaño",
        placeholder = "Seleccioná un tamaño",
        enabled = enabled,
        modifier = modifier
    )
}
