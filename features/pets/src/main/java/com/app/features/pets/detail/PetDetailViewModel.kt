package com.app.features.pets.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.usecase.GetPetByIdUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PetDetailViewModel(
    private val getPetByIdUseCase: GetPetByIdUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<PetDetailUiState>(
            PetDetailUiState.Loading
        )

    val uiState: StateFlow<PetDetailUiState> =
        _uiState.asStateFlow()

    fun loadPet(petId: String) {

        viewModelScope.launch {

            _uiState.value = PetDetailUiState.Loading

            try {

                val pet = getPetByIdUseCase(petId)

                if (pet != null) {

                    _uiState.value = PetDetailUiState.Success(
                        pet = pet
                    )

                } else {

                    _uiState.value = PetDetailUiState.Error(
                        message = "No se encontró la mascota"
                    )
                }

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {

                _uiState.value = PetDetailUiState.Error(
                    message = e.message
                        ?: "Error al consultar la mascota"
                )
            }
        }
    }
}