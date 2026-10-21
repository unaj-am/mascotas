
package com.app.features.pets.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.model.Pet
import com.app.core.domain.usecase.DeletePetUseCase
import com.app.core.domain.usecase.GetPetsByOwnerUseCase
import com.app.core.domain.usecase.SavePetUseCase
import com.app.core.domain.usecase.UpdatePetUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface PetOperationUiState {
    data object Idle : PetOperationUiState
    data object Loading : PetOperationUiState
    data object Success : PetOperationUiState
    data class Error(val message: String) : PetOperationUiState
}

class PetViewModel(
    private val getPetsByOwnerUseCase: GetPetsByOwnerUseCase,
    private val savePetUseCase: SavePetUseCase,
    private val updatePetUseCase: UpdatePetUseCase,
    private val deletePetUseCase: DeletePetUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<PetListUiState>(PetListUiState.Loading)

    val uiState: StateFlow<PetListUiState> =
        _uiState.asStateFlow()

    private val _operationState =
        MutableStateFlow<PetOperationUiState>(
            PetOperationUiState.Idle
        )

    val operationState: StateFlow<PetOperationUiState> =
        _operationState.asStateFlow()

    fun loadPets(ownerId: String) {
        viewModelScope.launch {
            _uiState.value = PetListUiState.Loading

            try {
                val pets = getPetsByOwnerUseCase(ownerId)
                _uiState.value = PetListUiState.Success(pets)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = PetListUiState.Error(
                    e.message ?: "Error al cargar las mascotas"
                )
            }
        }
    }

    fun savePet(pet: Pet) {
        executeOperation(pet.ownerId) {
            savePetUseCase(pet)
        }
    }

    fun updatePet(pet: Pet) {
        executeOperation(pet.ownerId) {
            updatePetUseCase(pet)
        }
    }

    fun deletePet(petId: String, ownerId: String) {
        executeOperation(ownerId) {
            deletePetUseCase(petId)
        }
    }

    fun resetOperationState() {
        _operationState.value = PetOperationUiState.Idle
    }

    private fun executeOperation(
        ownerId: String,
        operation: suspend () -> Unit
    ) {
        // Evita ejecutar dos operaciones simultáneamente.
        if (_operationState.value is PetOperationUiState.Loading) {
            return
        }

        viewModelScope.launch {
            _operationState.value = PetOperationUiState.Loading

            try {
                // 1. Ejecutar la escritura en el repositorio.
                operation()

                // 2. Actualizar el listado desde la fuente de datos.
                try {
                    val pets = getPetsByOwnerUseCase(ownerId)
                    _uiState.value = PetListUiState.Success(pets)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    // La escritura ya se realizó. El error corresponde
                    // a la recarga del listado, no al guardado.
                    _uiState.value = PetListUiState.Error(
                        e.message
                            ?: "La operación se realizó, pero no se pudo actualizar el listado."
                    )
                }

                // 3. Informar el resultado de la operación.
                // Se establece después de intentar actualizar el listado.
                _operationState.value = PetOperationUiState.Success

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _operationState.value = PetOperationUiState.Error(
                    e.message ?: "No se pudo completar la operación"
                )
            }
        }
    }
}
