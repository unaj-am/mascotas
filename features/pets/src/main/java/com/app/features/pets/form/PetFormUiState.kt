package com.app.features.pets.form

sealed interface PetFormUiState {

    data object Idle : PetFormUiState

    data object Loading : PetFormUiState

    data object Success : PetFormUiState

    data class Error(
        val message: String
    ) : PetFormUiState
}