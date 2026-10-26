
package com.app.features.walkers.requests

import com.app.core.domain.model.WalkRequest

data class WalkerRequestsUiState(
    val walkerId: String = "",
    val requests: List<WalkRequest> = emptyList(),
    val isLoading: Boolean = false,
    val loadingError: String? = null,
    val processingRequestId: String? = null,
    val operationMessage: String? = null,
    val operationError: String? = null
)
