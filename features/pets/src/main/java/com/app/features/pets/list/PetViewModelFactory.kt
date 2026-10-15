
package com.app.features.pets.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.core.domain.usecase.DeletePetUseCase
import com.app.core.domain.usecase.GetPetsByOwnerUseCase
import com.app.core.domain.usecase.SavePetUseCase
import com.app.core.domain.usecase.UpdatePetUseCase

class PetViewModelFactory(
    private val getPetsByOwnerUseCase: GetPetsByOwnerUseCase,
    private val savePetUseCase: SavePetUseCase,
    private val updatePetUseCase: UpdatePetUseCase,
    private val deletePetUseCase: DeletePetUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(PetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PetViewModel(
                getPetsByOwnerUseCase = getPetsByOwnerUseCase,
                savePetUseCase = savePetUseCase,
                updatePetUseCase = updatePetUseCase,
                deletePetUseCase = deletePetUseCase
            ) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}
