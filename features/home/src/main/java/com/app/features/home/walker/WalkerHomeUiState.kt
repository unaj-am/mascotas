
package com.app.features.home.walker

import com.app.core.domain.model.WalkRequest

data class WalkerHomeUiState(
    val userName: String = "",
    val profileImage: String? = null,
    val available: Boolean = false,

    // Cantidad de solicitudes pendientes.
    val pendingRequests: Int = 0,

    // Solicitudes pendientes que se mostrarán en el Home.
    val pendingRequestItems: List<WalkRequest> = emptyList(),

    val scheduledWalks: Int = 0,
    val activeWalks: Int = 0,
    val rating: Double? = null,
    val isLoading: Boolean = true,
    val isUpdatingAvailability: Boolean = false,
    val errorMessage: String? = null,
    val availabilityMessage: String? = null
)
