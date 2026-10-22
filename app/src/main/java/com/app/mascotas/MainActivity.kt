
package com.app.mascotas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.core.data.repository.PetRepositoryImpl
import com.app.core.domain.model.UserRole
import com.app.core.domain.usecase.DeletePetUseCase
import com.app.core.domain.usecase.GetPetByIdUseCase
import com.app.core.domain.usecase.GetPetsByOwnerUseCase
import com.app.core.domain.usecase.SavePetUseCase
import com.app.core.domain.usecase.UpdatePetUseCase
import com.app.core.ui.theme.MascotasThemeUI
import com.app.features.auth.login.LoginScreen
import com.app.features.auth.register.RegisterScreen
import com.app.features.home.OwnerBottomNavigation
import com.app.features.home.OwnerHomeScreen
import com.app.features.home.OwnerTab
import com.app.features.pets.detail.PetDetailRoute
import com.app.features.pets.detail.PetDetailViewModel
import com.app.features.pets.form.PetFormScreen
import com.app.features.pets.list.PetOperationUiState
import com.app.features.pets.list.PetRoute
import com.app.features.pets.list.PetViewModel
import com.app.features.pets.list.PetViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import com.app.core.ui.components.SuccessDialog
import androidx.compose.runtime.remember
import com.app.core.domain.model.Pet

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val petRepository by lazy {
        PetRepositoryImpl(
            firestore = FirebaseFirestore.getInstance(),
            firebaseAuth = FirebaseAuth.getInstance()
        )
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val petViewModel = ViewModelProvider(
            this,
            petViewModelFactory
        )[PetViewModel::class.java]

        val petDetailViewModel = PetDetailViewModel(
            getPetByIdUseCase
        )

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

                var selectedPetId by rememberSaveable {
                    mutableStateOf<String?>(null)
                }

                var showPetForm by rememberSaveable {
                    mutableStateOf(false)
                }

                var petBeingEdited by remember {
                    mutableStateOf<Pet?>(null)
                }

                var selectedOwnerTab by rememberSaveable {
                    mutableStateOf(OwnerTab.HOME)
                }

                var showPetSuccessDialog by rememberSaveable {
                    mutableStateOf(false)
                }

                var isCreatingPet by rememberSaveable {
                    mutableStateOf(false)
                }

                val operationState by
                petViewModel.operationState.collectAsStateWithLifecycle()

                LaunchedEffect(currentUserId, currentUserRole) {
                    if (
                        currentUserId != null &&
                        currentUserRole == UserRole.OWNER
                    ) {
                        petViewModel.loadPets(currentUserId!!)
                    }
                }

                // Cerrar el formulario después de guardar correctamente.
                LaunchedEffect(operationState, showPetForm) {
                    if (showPetForm && operationState is PetOperationUiState.Success) {
                        showPetForm = false
                        selectedPetId = null
                        petBeingEdited = null

                        if (isCreatingPet) {
                            showPetSuccessDialog = true
                        }

                        isCreatingPet = false

                        petViewModel.resetOperationState()
                        selectedOwnerTab = OwnerTab.PETS
                    }
                }

                when {
                    currentUserId != null &&
                            currentUserRole == UserRole.OWNER -> {

                        val ownerId = currentUserId!!

                        when {
                            showPetForm -> {
                                BackHandler(
                                    enabled = operationState !is PetOperationUiState.Loading
                                ) {
                                    showPetForm = false
                                    petBeingEdited = null
                                    petViewModel.resetOperationState()
                                }

                                val errorMessage =
                                    (operationState as? PetOperationUiState.Error)?.message

                                PetFormScreen(
                                    ownerId = ownerId,
                                    pet = petBeingEdited,
                                    isSaving = operationState is PetOperationUiState.Loading,
                                    errorMessage = errorMessage,
                                    onSave = { pet ->
                                        if (petBeingEdited != null) {
                                            petViewModel.updatePet(pet)
                                        } else {
                                            petViewModel.savePet(pet)
                                        }
                                    },
                                    onCancel = {
                                        if (operationState !is PetOperationUiState.Loading) {
                                            showPetForm = false
                                            petBeingEdited = null
                                            petViewModel.resetOperationState()
                                        }
                                    }
                                )
                            }

                            selectedPetId != null -> {
                                BackHandler {
                                    selectedPetId = null
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .safeDrawingPadding()
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
                                        viewModel = petDetailViewModel,
                                        onEditClick = { pet ->
                                            petViewModel.resetOperationState()
                                            petBeingEdited = pet
                                            isCreatingPet = false
                                            showPetForm = true
                                        }
                                    )
                                }
                            }

                            else -> {
                                Scaffold(
                                    modifier = Modifier.fillMaxSize(),
                                    bottomBar = {
                                        OwnerBottomNavigation(
                                            selectedTab = selectedOwnerTab,
                                            onTabSelected = { tab ->
                                                selectedOwnerTab = tab
                                            }
                                        )
                                    }
                                ) { innerPadding ->

                                    when (selectedOwnerTab) {
                                        OwnerTab.HOME -> {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(innerPadding)
                                            ) {
                                                OwnerHomeScreen(
                                                    userName = "Dueño",
                                                    petsCount = 0,
                                                    upcomingWalksCount = 0,
                                                    nextPetName = null,
                                                    nextPetBreed = null,
                                                    nextWalkDate = null,
                                                    onNotificationsClick = {
                                                        // Pendiente: módulo de notificaciones.
                                                    },
                                                    onPetsClick = {
                                                        selectedOwnerTab = OwnerTab.PETS
                                                    },
                                                    onWalksClick = {
                                                        selectedOwnerTab = OwnerTab.WALKS
                                                    },
                                                    onFindWalkerClick = {
                                                        selectedOwnerTab = OwnerTab.WALKS
                                                    },
                                                    onHistoryClick = {
                                                        selectedOwnerTab = OwnerTab.WALKS
                                                    }
                                                )
                                            }
                                        }

                                        OwnerTab.PETS -> {
                                            PetRoute(
                                                ownerId = ownerId,
                                                viewModel = petViewModel,
                                                modifier = Modifier.padding(innerPadding),
                                                onPetClick = { petId ->
                                                    selectedPetId = petId
                                                },
                                                onAddPetClick = {
                                                    petViewModel.resetOperationState()
                                                    isCreatingPet = true
                                                    showPetForm = true
                                                }
                                            )
                                        }

                                        OwnerTab.WALKS -> {
                                            PlaceholderScreen(
                                                title = "Mis paseos",
                                                description =
                                                    "El módulo de paseos se integrará en este apartado.",
                                                modifier = Modifier.padding(
                                                    innerPadding
                                                )
                                            )
                                        }

                                        OwnerTab.PROFILE -> {
                                            PlaceholderScreen(
                                                title = "Mi perfil",
                                                description =
                                                    "El módulo de perfil se integrará en este apartado.",
                                                modifier = Modifier.padding(
                                                    innerPadding
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }



                    currentUserId != null &&
                            currentUserRole == UserRole.WALKER -> {

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .safeDrawingPadding()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Panel del paseador pendiente de integración")
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
                                selectedOwnerTab = OwnerTab.HOME
                            },
                            onNavigateToRegister = {
                                showRegister = true
                            }
                        )
                    }


                }
                if (showPetSuccessDialog) {
                    SuccessDialog(
                        title = "Mascota registrada",
                        message = "La mascota se guardó correctamente.",
                        confirmText = "Aceptar",
                        onConfirm = {
                            showPetSuccessDialog = false
                        },
                        onDismiss = {
                            showPetSuccessDialog = false
                        }
                    )
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun PlaceholderScreen(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Text(text = title)
        Text(
            text = description,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
