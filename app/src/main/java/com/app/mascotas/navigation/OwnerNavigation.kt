
package com.app.mascotas.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.core.domain.model.Pet
import com.app.core.ui.components.SuccessDialog
import com.app.features.pets.detail.PetDetailRoute
import com.app.features.pets.detail.PetDetailViewModel
import com.app.features.pets.form.PetFormScreen
import com.app.features.pets.list.PetOperationUiState
import com.app.features.pets.list.PetViewModel
import com.app.mascotas.ui.AppShell

@Composable
fun OwnerNavigation(
    ownerId: String,
    petViewModel: PetViewModel,
    petDetailViewModel: PetDetailViewModel
) {
    val navigationState = rememberNavigationState()

    var selectedPetId by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var showPetForm by rememberSaveable {
        mutableStateOf(false)
    }

    var petBeingEdited by remember {
        mutableStateOf<Pet?>(null)
    }

    var isCreatingPet by rememberSaveable {
        mutableStateOf(false)
    }

    var showPetSuccessDialog by rememberSaveable {
        mutableStateOf(false)
    }

    val operationState by
    petViewModel.operationState.collectAsStateWithLifecycle()

    LaunchedEffect(operationState, showPetForm) {
        if (
            showPetForm &&
            operationState is PetOperationUiState.Success
        ) {
            showPetForm = false
            selectedPetId = null
            petBeingEdited = null

            if (isCreatingPet) {
                showPetSuccessDialog = true
            }

            isCreatingPet = false
            petViewModel.resetOperationState()

            navigationState.selectTab(
                TopLevelDestination.PETS
            )
        }
    }

    when {
        showPetForm -> {
            BackHandler(
                enabled = operationState !is
                        PetOperationUiState.Loading
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
                isSaving = operationState is
                        PetOperationUiState.Loading,
                errorMessage = errorMessage,
                onSave = { pet ->
                    if (petBeingEdited != null) {
                        petViewModel.updatePet(pet)
                    } else {
                        petViewModel.savePet(pet)
                    }
                },
                onCancel = {
                    if (
                        operationState !is
                                PetOperationUiState.Loading
                    ) {
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
                    modifier = Modifier.padding(horizontal = 8.dp)
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
            AppShell(
                navigationState = navigationState,
                ownerId = ownerId,
                petViewModel = petViewModel,
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
