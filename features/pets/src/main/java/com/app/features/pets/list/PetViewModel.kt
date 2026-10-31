
package com.app.features.pets.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.model.Pet
import com.app.core.domain.usecase.DeletePetUseCase
import com.app.core.domain.usecase.GetPetsByOwnerUseCase
import com.app.core.domain.usecase.SavePetUseCase
import com.app.core.domain.usecase.UpdatePetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.app.cloudinary.CloudinaryPhotoUploader
import android.net.Uri

sealed interface PetOperationUiState {
    data object Idle : PetOperationUiState
    data object Loading : PetOperationUiState
    data object Success : PetOperationUiState
    data class Error(val message: String) : PetOperationUiState
}

@HiltViewModel
class PetViewModel @Inject constructor(
    private val getPetsByOwnerUseCase: GetPetsByOwnerUseCase,
    private val savePetUseCase: SavePetUseCase,
    private val updatePetUseCase: UpdatePetUseCase,
    private val deletePetUseCase: DeletePetUseCase,
    private val cloudinaryPhotoUploader: CloudinaryPhotoUploader
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

    suspend fun uploadPetPhoto(uri: Uri): String {
        return cloudinaryPhotoUploader.uploadPhoto(uri)
    }

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

    fun savePet(pet: Pet, photoUri: Uri? = null) {
        executeOperation(pet.ownerId) {
            val petToSave = if (photoUri != null) {
                val imageUrl = uploadPetPhoto(photoUri)
                pet.copy(photoUri = imageUrl)
            } else {
                pet
            }

            savePetUseCase(petToSave)
        }
    }

    fun updatePet(pet: Pet, photoUri: Uri? = null) {
        executeOperation(pet.ownerId) {
            val petToUpdate = if (photoUri != null) {
                val imageUrl = uploadPetPhoto(photoUri)
                pet.copy(photoUri = imageUrl)
            } else {
                pet
            }

            updatePetUseCase(petToUpdate)
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
        if (_operationState.value is PetOperationUiState.Loading) {
            return
        }

        viewModelScope.launch {
            _operationState.value = PetOperationUiState.Loading

            try {
                operation()

                try {
                    val pets = getPetsByOwnerUseCase(ownerId)
                    _uiState.value = PetListUiState.Success(pets)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    _uiState.value = PetListUiState.Error(
                        e.message
                            ?: "La operación se realizó, pero no se pudo actualizar el listado."
                    )
                }

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
