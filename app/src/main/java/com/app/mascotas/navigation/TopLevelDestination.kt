
package com.app.mascotas.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey

enum class TopLevelDestination(
    val startKey: NavKey,
    val label: String,
    val icon: ImageVector
) {
    HOME(
        startKey = HomeDestination,
        label = "Inicio",
        icon = Icons.Filled.Home
    ),

    PETS(
        startKey = PetsDestination,
        label = "Mascotas",
        icon = Icons.Filled.Pets
    ),

    WALKS(
        startKey = WalksDestination,
        label = "Paseos",
        icon = Icons.Filled.DirectionsWalk
    ),

    PROFILE(
        startKey = ProfileDestination,
        label = "Perfil",
        icon = Icons.Filled.Person
    )
}
