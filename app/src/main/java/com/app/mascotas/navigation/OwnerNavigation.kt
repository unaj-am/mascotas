
package com.app.mascotas.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.core.ui.components.SuccessDialog
import com.app.features.pets.detail.PetDetailViewModel
import com.app.features.pets.list.PetOperationUiState
import com.app.features.pets.list.PetViewModel
import com.app.features.walkers.list.WalkerViewModel
import com.app.mascotas.ui.AppShell
import com.app.mascotas.ui.AppTab

@Composable
fun OwnerNavigation(
    ownerId: String,
    onSignOut: () -> Unit,
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit
) {
    val petViewModel: PetViewModel = hiltViewModel()
    val petDetailViewModel: PetDetailViewModel = hiltViewModel()
    val walkerViewModel: WalkerViewModel = hiltViewModel()

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

    val tabs = TopLevelDestination.entries.map { destination ->
        AppTab(
            key = destination.startKey,
            label = destination.label,
            icon = destination.icon
        )
    }

    AppShell(
        tabs = tabs,
        currentTab = navigationState.currentTab.startKey,
        onTabClick = { selectedKey ->
            TopLevelDestination.entries
                .firstOrNull { it.startKey == selectedKey }
                ?.let(navigationState::selectTab)
        },
        isWalker = false
    ) { innerPadding ->
        AppNavDisplay(
            navigationState = navigationState,
            ownerId = ownerId,
            petViewModel = petViewModel,
            petDetailViewModel = petDetailViewModel,
            walkerViewModel = walkerViewModel,
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
            onSignOut = onSignOut,
            darkTheme = darkTheme,
            onDarkThemeChange = onDarkThemeChange,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
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
