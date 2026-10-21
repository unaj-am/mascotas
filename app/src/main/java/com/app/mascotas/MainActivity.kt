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
import androidx.room.Room
import com.app.core.data.repository.PetRepositoryImpl
import com.app.core.database.AppDatabase
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
import com.app.core.ui.theme.MascotasThemeUI
import dagger.hilt.android.AndroidEntryPoint
import com.app.core.domain.model.UserRole
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

        setContent {
            MascotasThemeUI {

                var currentUserId by rememberSaveable {
                    mutableStateOf<String?>(null)
                }

                var currentUserRole by rememberSaveable {
                    mutableStateOf<UserRole?>(null)
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
                    currentUserId != null && currentUserRole == UserRole.OWNER -> {
                        val ownerId = currentUserId!!

                        LaunchedEffect(ownerId) {
                            petViewModel.loadPets(ownerId)
                        }

                        when {
                            showPetForm -> {
                                BackHandler {
                                    showPetForm = false
                                    petViewModel.resetOperationState()
                                }

                                val errorMessage =
                                    (operationState as? PetOperationUiState.Error)?.message

                                PetFormScreen(
                                    ownerId = ownerId,
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
                                        modifier = Modifier.padding(horizontal = 8.dp)
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
                                    ownerId = ownerId,
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

                    currentUserId != null && currentUserRole == UserRole.WALKER -> {
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier.safeDrawingPadding()
                                .padding(24.dp)
                        ) {
                            Text("Panel del paseador")
                        }
                    }

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

                    else -> {
                        LoginScreen(
                            onLoginSuccess = { userId, role ->
                                currentUserId = userId
                                currentUserRole = role
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