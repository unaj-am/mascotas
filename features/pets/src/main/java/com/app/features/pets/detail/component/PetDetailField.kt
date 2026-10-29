
package com.app.features.pets.detail.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.ui.components.InfoRow

/**
 * Componente de compatibilidad para mostrar un dato
 * en el detalle de una mascota.
 *
 * Delegamos la presentación visual a InfoRow,
 * centralizando los estilos en core:ui.
 */
@Composable
fun PetDetailField(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    InfoRow(
        label = label,
        value = value,
        modifier = modifier
    )
}
