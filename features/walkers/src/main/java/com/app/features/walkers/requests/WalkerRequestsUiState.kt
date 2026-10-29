
package com.app.features.walkers.requests

import com.app.core.domain.model.WalkRequest

data class WalkerRequestsUiState(
    val walkerId: String = "",
    val requests: List<WalkRequest> = emptyList(),

    // Nombres de los dueños, indexados por ID de solicitud.
    val ownerNames: Map<String, String> = emptyMap(),

    // Nombres de las mascotas, indexados por ID de solicitud.
    val petNames: Map<String, List<String>> = emptyMap(),

    val isLoading: Boolean = false,
    val loadingError: String? = null,
    val processingRequestId: String? = null,
    val operationMessage: String? = null,
    val operationError: String? = null
)
