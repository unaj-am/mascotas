package com.app.features.pets.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.core.domain.usecase.GetPetByIdUseCase

class PetDetailViewModelFactory(
    private val getPetByIdUseCase: GetPetByIdUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(PetDetailViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return PetDetailViewModel(
                getPetByIdUseCase
            ) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}