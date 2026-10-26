
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
data object WalkerSearchDestination : NavKey

@Serializable
data object ProfileDestination : NavKey

@Serializable
data class PetDetailDestination(
    val petId: String
) : NavKey

@Serializable
data class PetFormDestination(
    val ownerId: String,
    val petId: String? = null
) : NavKey

// Destinos del paseador

@Serializable
data object WalkerHomeDestination : NavKey

@Serializable
data object WalkerAgendaDestination : NavKey

@Serializable
data object WalkerActiveWalkDestination : NavKey

@Serializable
data object WalkerProfileDestination : NavKey

@Serializable
data object WalkerRequestsDestination : NavKey

@Serializable
data object WalkerHistoryDestination : NavKey
