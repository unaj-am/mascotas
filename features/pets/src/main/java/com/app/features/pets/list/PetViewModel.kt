package com.app.features.pets.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.usecase.GetPetsByOwnerUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PetViewModel(
    private val getPetsByOwnerUseCase: GetPetsByOwnerUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<PetListUiState>(
            PetListUiState.Loading
        )

    val uiState: StateFlow<PetListUiState> =
        _uiState.asStateFlow()

    fun loadPets(ownerId: String) {

        viewModelScope.launch {

            _uiState.value = PetListUiState.Loading

            try {
                val pets = getPetsByOwnerUseCase(ownerId)

                _uiState.value = PetListUiState.Success(
                    pets = pets
                )

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {

                _uiState.value = PetListUiState.Error(
                    message = e.message
                        ?: "Error al cargar mascotas"
                )
            }
        }
    }
}