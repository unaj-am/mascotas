
package com.app.mascotas.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object HomeDestination : NavKey

@Serializable
data object PetsDestination : NavKey

@Serializable
data object WalksDestination : NavKey

@Serializable
data object ProfileDestination : NavKey
