package com.app.mascotas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.app.core.data.repository.PetRepositoryImpl
import com.app.core.domain.usecase.GetPetsByOwnerUseCase
import com.app.features.pets.list.PetRoute
import com.app.features.pets.list.PetViewModel
import com.app.features.pets.list.PetViewModelFactory
import com.app.mascotas.ui.theme.MascotasTheme

class MainActivity : ComponentActivity() {

    private val petRepository = PetRepositoryImpl()

    private val getPetsByOwnerUseCase =
        GetPetsByOwnerUseCase(petRepository)

    private val petViewModelFactory =
        PetViewModelFactory(getPetsByOwnerUseCase)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val petViewModel = ViewModelProvider(
            this,
            petViewModelFactory
        )[PetViewModel::class.java]

        setContent {
            MascotasTheme {
                PetRoute(
                    ownerId = "USR001",
                    viewModel = petViewModel
                )
            }
        }
    }
}