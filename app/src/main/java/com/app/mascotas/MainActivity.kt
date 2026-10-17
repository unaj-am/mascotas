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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.app.core.data.repository.PetRepositoryImpl
import com.app.core.database.AppDatabase
import com.app.core.domain.model.Pet
import com.app.core.domain.usecase.DeletePetUseCase
import com.app.core.domain.usecase.GetPetByIdUseCase
import com.app.core.domain.usecase.GetPetsByOwnerUseCase
import com.app.core.domain.usecase.SavePetUseCase
import com.app.core.domain.usecase.UpdatePetUseCase
import com.app.features.auth.login.LoginScreen
import com.app.features.auth.register.RegisterScreen
import com.app.features.pets.detail.PetDetailRoute
import com.app.features.pets.detail.PetDetailViewModel
import com.app.features.pets.detail.PetDetailViewModelFactory
import com.app.features.pets.form.PetFormScreen
import com.app.features.pets.list.PetOperationUiState
import com.app.features.pets.list.PetRoute
import com.app.features.pets.list.PetViewModel
import com.app.features.pets.list.PetViewModelFactory
import com.app.mascotas.ui.theme.MascotasTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "mascotas_database"
        ).build()
    }

    private val petRepository by lazy {
        PetRepositoryImpl(database.petDao())
    }

    private val getPetsByOwnerUseCase by lazy {
        GetPetsByOwnerUseCase(petRepository)
    }

    private val getPetByIdUseCase by lazy {
        GetPetByIdUseCase(petRepository)
    }

    private val savePetUseCase by lazy {
        SavePetUseCase(petRepository)
    }

    private val updatePetUseCase by lazy {
        UpdatePetUseCase(petRepository)
    }

    private val deletePetUseCase by lazy {
        DeletePetUseCase(petRepository)
    }

    private val petViewModelFactory by lazy {
        PetViewModelFactory(
            getPetsByOwnerUseCase,
            savePetUseCase,
            updatePetUseCase,
            deletePetUseCase
        )
    }

    private val petDetailViewModelFactory by lazy {
        PetDetailViewModelFactory(getPetByIdUseCase)
    }

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

        // Datos de prueba: insertar solamente si la base está vacía.
        lifecycleScope.launch {
            val mascotasExistentes =
                petRepository.getPetsByOwner("USR001")

            if (mascotasExistentes.isEmpty()) {
                petRepository.savePet(
                    Pet(
                        id = "PET001",
                        ownerId = "USR001",
                        name = "Firulais",
                        breed = "Labrador",
                        age= 3,
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
            }

            petViewModel.loadPets("USR001")
        }

        setContent {
            MascotasTheme {

                var isLoggedIn by rememberSaveable {
                    mutableStateOf(false)
                }

                var showRegister by rememberSaveable {
                    mutableStateOf(false)
                }

                // Estado de la pantalla de mascotas.
                var selectedPetId by rememberSaveable {
                    mutableStateOf<String?>(null)
                }

                var showPetForm by rememberSaveable {
                    mutableStateOf(false)
                }

                val operationState by
                petViewModel.operationState.collectAsStateWithLifecycle()

                // Volver a la lista cuando el guardado finaliza correctamente.
                LaunchedEffect(operationState, showPetForm) {
                    if (
                        showPetForm &&
                        operationState is PetOperationUiState.Success
                    ) {
                        showPetForm = false
                        selectedPetId = null
                        petViewModel.resetOperationState()
                    }
                }

                when {
                    // Pantalla principal de mascotas.
                    isLoggedIn -> {
                        when {
                            showPetForm -> {
                                BackHandler {
                                    showPetForm = false
                                    petViewModel.resetOperationState()
                                }

                                val errorMessage =
                                    (operationState as? PetOperationUiState.Error)
                                        ?.message

                                PetFormScreen(
                                    ownerId = "USR001",
                                    errorMessage = errorMessage,
                                    onSave = { pet ->
                                        petViewModel.savePet(pet)
                                    },
                                    onCancel = {
                                        showPetForm = false
                                        petViewModel.resetOperationState()
                                    }
                                )
                            }

                            selectedPetId != null -> {
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
                                        petId = selectedPetId!!,
                                        viewModel = petDetailViewModel
                                    )
                                }
                            }

                            else -> {
                                PetRoute(
                                    ownerId = "USR001",
                                    viewModel = petViewModel,
                                    onPetClick = { petId ->
                                        selectedPetId = petId
                                    },
                                    onAddPetClick = {
                                        petViewModel.resetOperationState()
                                        showPetForm = true
                                    }
                                )
                            }
                        }
                    }

                    // Pantalla de registro.
                    showRegister -> {
                        BackHandler {
                            showRegister = false
                        }

                        RegisterScreen(
                            onRegisterSuccess = {
                                showRegister = false
                            },
                            onNavigateToLogin = {
                                showRegister = false
                            }
                        )
                    }

                    // Pantalla de inicio de sesión.
                    else -> {
                        LoginScreen(
                            onLoginSuccess = {
                                isLoggedIn = true
                            },
                            onNavigateToRegister = {
                                showRegister = true
                            }
                        )
                    }
                }
            }
        }
    }
}