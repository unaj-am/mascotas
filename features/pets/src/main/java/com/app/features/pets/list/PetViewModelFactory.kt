package com.app.features.pets.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.core.domain.usecase.GetPetsByOwnerUseCase

class PetViewModelFactory(
    private val getPetsByOwnerUseCase: GetPetsByOwnerUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(PetViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return PetViewModel(getPetsByOwnerUseCase) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}