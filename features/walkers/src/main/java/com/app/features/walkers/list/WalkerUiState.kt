package com.app.features.walkers.list

import com.app.core.domain.model.Walker

sealed interface WalkerUiState {

    data object Loading : WalkerUiState

    data class Success(
        val walkers: List<Walker>
    ) : WalkerUiState

    data class Error(
        val message: String
    ) : WalkerUiState
}