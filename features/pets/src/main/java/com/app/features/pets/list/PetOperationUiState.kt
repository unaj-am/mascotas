package com.app.features.pets.list

sealed interface PetOperationUiState {
    data object Idle : PetOperationUiState
    data object Loading : PetOperationUiState
    data object Success : PetOperationUiState
    data class Error(val message: String) : PetOperationUiState
}
