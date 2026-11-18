
package com.app.features.pets.form

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.model.Pet
import com.app.core.domain.service.PhotoUploader
import com.app.core.domain.usecase.SavePetUseCase
import com.app.core.domain.usecase.UpdatePetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PetFormViewModel @Inject constructor(
    private val savePetUseCase: SavePetUseCase,
    private val updatePetUseCase: UpdatePetUseCase,
    private val photoUploader: PhotoUploader
) : ViewModel() {

    private val _uiState = MutableStateFlow<PetFormUiState>(PetFormUiState.Idle)

    val uiState: StateFlow<PetFormUiState> =
        _uiState.asStateFlow()

    fun savePet(
        pet: Pet,
        photoUri: Uri? = null
    ) {
        executeOperation {
            val petToSave = if (photoUri != null) {
                val imageUrl = uploadPetPhoto(photoUri)
                pet.copy(photoUri = imageUrl)
            } else {
                pet
            }

            savePetUseCase(petToSave)
        }
    }

    fun updatePet(
        pet: Pet,
        photoUri: Uri? = null
    ) {
        executeOperation {
            val petToUpdate = if (photoUri != null) {
                val imageUrl = uploadPetPhoto(photoUri)
                pet.copy(photoUri = imageUrl)
            } else {
                pet
            }

            updatePetUseCase(petToUpdate)
        }
    }

    private suspend fun uploadPetPhoto(uri: Uri): String {
        return photoUploader.uploadPhoto(uri.toString())
    }

    private fun executeOperation(
        operation: suspend () -> Unit
    ) {
        if (_uiState.value is PetFormUiState.Loading) {
            return
        }

        viewModelScope.launch {
            _uiState.value = PetFormUiState.Loading

            try {
                operation()

                _uiState.value = PetFormUiState.Success

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                _uiState.value = PetFormUiState.Error(
                    e.message ?: "No se pudo completar la operación"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = PetFormUiState.Idle
    }
}
