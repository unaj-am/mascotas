package com.app.mascotas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.app.core.data.repository.PetRepositoryImpl
import com.app.core.domain.model.Pet
import com.app.core.domain.usecase.GetPetByIdUseCase
import com.app.core.domain.usecase.GetPetsByOwnerUseCase
import com.app.features.pets.detail.PetDetailRoute
import com.app.features.pets.detail.PetDetailViewModel
import com.app.features.pets.detail.PetDetailViewModelFactory
import com.app.features.pets.list.PetRoute
import com.app.features.pets.list.PetViewModel
import com.app.features.pets.list.PetViewModelFactory
import com.app.mascotas.ui.theme.MascotasTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val petRepository = PetRepositoryImpl()

    private val getPetsByOwnerUseCase =
        GetPetsByOwnerUseCase(petRepository)

    private val getPetByIdUseCase =
        GetPetByIdUseCase(petRepository)

    private val petViewModelFactory =
        PetViewModelFactory(getPetsByOwnerUseCase)

    private val petDetailViewModelFactory =
        PetDetailViewModelFactory(getPetByIdUseCase)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val petViewModel = ViewModelProvider(
            this,
            petViewModelFactory
        )[PetViewModel::class.java]

        val petDetailViewModel = ViewModelProvider(
            this,
            petDetailViewModelFactory
        )[PetDetailViewModel::class.java]

        // Datos de prueba
        lifecycleScope.launch {

            petRepository.savePet(
                Pet(
                    id = "PET001",
                    ownerId = "USR001",
                    name = "Firulais",
                    breed = "Labrador",
                    age = 3,
                    weight = 25.0,
                    observations = "Es sociable y tranquilo"
                )
            )

            petRepository.savePet(
                Pet(
                    id = "PET002",
                    ownerId = "USR001",
                    name = "Luna",
                    breed = "Mestiza",
                    age = 2,
                    weight = 12.5,
                    observations = "Le gusta jugar y pasear"
                )
            )

            // Actualizamos el listado después de guardar
            petViewModel.loadPets("USR001")
        }

        setContent {

            MascotasTheme {

                var selectedPetId by rememberSaveable {
                    mutableStateOf<String?>(null)
                }

                val currentPetId = selectedPetId

                if (currentPetId == null) {

                    PetRoute(
                        ownerId = "USR001",
                        viewModel = petViewModel,
                        onPetClick = { petId ->
                            selectedPetId = petId
                        }
                    )

                } else {

                    BackHandler {
                        selectedPetId = null
                    }

                    Column(
                        modifier = Modifier.safeDrawingPadding()
                    ) {

                        TextButton(
                            onClick = {
                                selectedPetId = null
                            },
                            modifier = Modifier.padding(
                                horizontal = 8.dp
                            )
                        ) {
                            Text("← Volver a mis mascotas")
                        }

                        PetDetailRoute(
                            petId = currentPetId,
                            viewModel = petDetailViewModel
                        )
                    }
                }
            }
        }
    }
}