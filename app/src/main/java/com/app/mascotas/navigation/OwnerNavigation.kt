
package com.app.mascotas.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.core.ui.components.SuccessDialog
import com.app.features.pets.detail.PetDetailViewModel
import com.app.features.pets.list.PetOperationUiState
import com.app.features.pets.list.PetViewModel
import com.app.mascotas.ui.AppShell

@Composable
fun OwnerNavigation(
    ownerId: String,
    onSignOut: () -> Unit
) {
    val petViewModel: PetViewModel = hiltViewModel()
    val petDetailViewModel: PetDetailViewModel = hiltViewModel()

    val navigationState = rememberNavigationState()

    var showPetSuccessDialog by rememberSaveable {
        mutableStateOf(false)
    }

    val operationState by
    petViewModel.operationState.collectAsStateWithLifecycle()

    LaunchedEffect(
        operationState,
        navigationState.currentKey
    ) {
        val currentForm =
            navigationState.currentKey as? PetFormDestination

        if (
            currentForm != null &&
            operationState is PetOperationUiState.Success
        ) {
            val isCreatingPet = currentForm.petId == null

            navigationState.navigateToTabRoot(
                TopLevelDestination.PETS
            )

            petViewModel.resetOperationState()

            if (isCreatingPet) {
                showPetSuccessDialog = true
            }
        }
    }

    AppShell(
        navigationState = navigationState,
        ownerId = ownerId,
        petViewModel = petViewModel,
        petDetailViewModel = petDetailViewModel,
        onPetClick = { petId ->
            petViewModel.resetOperationState()

            navigationState.navigate(
                PetDetailDestination(petId)
            )
        },
        onAddPetClick = {
            petViewModel.resetOperationState()

            navigationState.navigate(
                PetFormDestination(ownerId = ownerId)
            )
        },
        onSignOut = onSignOut
    )

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
