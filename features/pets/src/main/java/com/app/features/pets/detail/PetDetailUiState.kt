package com.app.features.pets.detail

import com.app.core.domain.model.Pet

sealed interface PetDetailUiState {

    data object Loading : PetDetailUiState

    data class Success(
        val pet: Pet
    ) : PetDetailUiState

    data class Error(
        val message: String
    ) : PetDetailUiState
}