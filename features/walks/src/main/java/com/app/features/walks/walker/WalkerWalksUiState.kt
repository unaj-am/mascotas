package com.app.features.walks.walker

import com.app.core.domain.model.Walk

data class WalkerWalkDetails(
    val ownerName: String,
    val petNames: List<String>
)

data class WalkerWalksUiState(
    val walks: List<Walk> = emptyList(),
    val walkDetails: Map<String, WalkerWalkDetails> = emptyMap(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val operationMessage: String? = null,
    val operationError: String? = null,
    val processingId: String? = null
)