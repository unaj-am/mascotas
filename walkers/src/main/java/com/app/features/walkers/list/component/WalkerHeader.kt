
package com.app.features.walkers.list.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.ui.components.SectionHeader
import com.app.features.walkers.list.WalkerUiState

/**
 * Encabezado específico del listado de paseadores.
 *
 * Utiliza SectionHeader para mantener un estilo
 * compartido con el resto de las funcionalidades.
 */
@Composable
fun WalkerHeader(
    uiState: WalkerUiState,
    modifier: Modifier = Modifier
) {
    val subtitle = when (uiState) {
        WalkerUiState.Loading ->
            "Buscando paseadores..."

        is WalkerUiState.Success ->
            when (uiState.walkers.size) {
                0 -> "Todavía no hay paseadores registrados."
                1 -> "Encontramos 1 paseador."
                else -> "Encontramos ${uiState.walkers.size} paseadores."
            }

        is WalkerUiState.Error ->
            "No pudimos cargar los paseadores."
    }

    SectionHeader(
        title = "Encontrá tu paseador",
        subtitle = subtitle,
        modifier = modifier
    )
}
