package com.app.features.walks.owner

import com.app.core.domain.model.Walk

data class OwnerWalkDetailUiState(
    val walk: Walk? = null,
    val walkerName: String? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)