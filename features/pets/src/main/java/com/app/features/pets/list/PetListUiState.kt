package com.app.features.pets.list

import com.app.core.domain.model.Pet

sealed interface PetListUiState {

    data object Loading : PetListUiState

    data class Success(
        val pets: List<Pet>
    ) : PetListUiState

    data class Error(
        val message: String
    ) : PetListUiState
}