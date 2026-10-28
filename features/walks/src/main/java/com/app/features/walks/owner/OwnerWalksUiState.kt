package com.app.features.walks.owner

import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkRequest

data class OwnerWalksUiState(
    val walks: List<Walk> = emptyList(),
    val requests: List<WalkRequest> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val operationMessage: String? = null,
    val operationError: String? = null,
    val processingId: String? = null
)
