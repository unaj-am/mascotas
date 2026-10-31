
package com.app.mascotas.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.app.core.domain.model.Pet
import com.app.core.ui.components.ErrorContent
import com.app.core.ui.components.LoadingContent
import com.app.features.home.owner.OwnerHomeRoute
import com.app.features.pets.detail.PetDetailRoute
import com.app.features.pets.detail.PetDetailUiState
import com.app.features.pets.detail.PetDetailViewModel
import com.app.features.pets.form.PetFormScreen
import com.app.features.pets.list.PetOperationUiState
import com.app.features.pets.list.PetRoute
import com.app.features.pets.list.PetViewModel
import com.app.features.profile.OwnerProfileRoute
import com.app.features.tracking.WalkTrackingScreen
import com.app.features.walkers.list.WalkerRoute
import com.app.features.walkers.list.WalkerViewModel
import com.app.features.walkers.requestform.WalkRequestFormRoute
import com.app.features.walks.owner.OwnerWalkDetailRoute
import com.app.features.walks.owner.OwnerWalksRoute
@Composable
fun AppNavDisplay(
    navigationState: NavigationState,
    ownerId: String,
    petViewModel: PetViewModel,
    petDetailViewModel: PetDetailViewModel,
    walkerViewModel: WalkerViewModel,
    onPetClick: (String) -> Unit,
    onAddPetClick: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val detailUiState by
    petDetailViewModel.uiState.collectAsStateWithLifecycle()

    val operationState by
    petViewModel.operationState.collectAsStateWithLifecycle()

    LaunchedEffect(ownerId) {
        petViewModel.loadPets(ownerId)
    }

    val currentForm =
        navigationState.currentKey as? PetFormDestination

    BackHandler(
        enabled = currentForm != null &&
                operationState is PetOperationUiState.Loading
    ) {
        // Evita abandonar el formulario durante el guardado.
    }

    val destinationProvider = entryProvider<NavKey> {

        entry<HomeDestination> {
            OwnerHomeRoute(
                ownerId = ownerId,
                isVisible = navigationState.currentTab == TopLevelDestination.HOME,
                onNotificationsClick = {
                    // Pendiente: conectar notificaciones.
                },
                onFindWalkerClick = {
                    navigationState.navigate(
                        WalkerSearchDestination
                    )
                }
            )
        }

        entry<PetsDestination> {
            PetRoute(
                ownerId = ownerId,
                viewModel = petViewModel,
                onPetClick = onPetClick,
                onAddPetClick = onAddPetClick
            )
        }

        entry<WalksDestination> {
            OwnerWalksRoute(
                ownerId = ownerId,
                onTrackWalk = { walkId ->
                    navigationState.navigate(
                        WalkTrackingDestination(walkId)
                    )
                },
                onOpenWalk = { walkId ->
                    navigationState.navigate(
                        OwnerWalkDetailDestination(walkId)
                    )
                }
            )
        }

        entry<OwnerWalkDetailDestination> { destination ->
            OwnerWalkDetailRoute(
                walkId = destination.walkId,
                onBack = {
                    navigationState.goBackFrom(destination)
                },
                onTrackWalk = { walkId ->
                    navigationState.navigate(
                        WalkTrackingDestination(walkId)
                    )
                }
            )
        }

        entry<WalkTrackingDestination> { destination ->
            WalkTrackingScreen(
                walkId = destination.walkId,
                onBack = {
                    navigationState.goBackFrom(destination)
                }
            )
        }

        entry<WalkerSearchDestination> {
            WalkerRoute(
                viewModel = walkerViewModel,
                onWalkerClick = { walkerId ->
                    navigationState.navigate(
                        WalkRequestFormDestination(
                            walkerId = walkerId
                        )
                    )
                },
                onBack = {
                    navigationState.goBack()
                }
            )
        }

        entry<WalkRequestFormDestination> { destination ->
            WalkRequestFormRoute(
                ownerId = ownerId,
                walkerId = destination.walkerId,
                onBack = {
                    navigationState.goBack()
                },
                onSuccess = {
                    navigationState.navigateToTabRoot(
                        TopLevelDestination.WALKS
                    )
                }
            )
        }

        entry<ProfileDestination> {
            OwnerProfileRoute(
                userId = ownerId,
                onSignOut = onSignOut
            )
        }

        entry<PetDetailDestination> { destination ->
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TextButton(
                    onClick = {
                        navigationState.goBackFrom(destination)
                    },
                    modifier = Modifier
                        .windowInsetsPadding(
                            WindowInsets.safeDrawing.only(
                                WindowInsetsSides.Top
                            )
                        )
                        .padding(horizontal = 8.dp)
                ) {
                    Text("← Volver a mis mascotas")
                }

                PetDetailRoute(
                    petId = destination.petId,
                    viewModel = petDetailViewModel,
                    onEditClick = { pet ->
                        petViewModel.resetOperationState()

                        navigationState.navigate(
                            PetFormDestination(
                                ownerId = ownerId,
                                petId = pet.id
                            )
                        )
                    }
                )
            }
        }

        entry<PetFormDestination> { destination ->
            val editingPet: Pet? =
                if (destination.petId == null) {
                    null
                } else {
                    (detailUiState as? PetDetailUiState.Success)
                        ?.pet
                        ?.takeIf {
                            it.id == destination.petId
                        }
                }

            LaunchedEffect(destination.petId) {
                destination.petId?.let { petId ->
                    petDetailViewModel.loadPet(petId)
                }
            }

            when {
                destination.petId == null -> {
                    PetFormScreen(
                        ownerId = destination.ownerId,
                        pet = null,
                        isSaving = operationState is
                                PetOperationUiState.Loading,
                        errorMessage = (
                                operationState as?
                                        PetOperationUiState.Error
                                )?.message,
                        onSave = { pet, photoUri ->
                            petViewModel.savePet(pet, photoUri)
                        },
                        onCancel = {
                            navigationState.goBack()
                        }
                    )
                }

                editingPet != null -> {
                    PetFormScreen(
                        ownerId = destination.ownerId,
                        pet = editingPet,
                        isSaving = operationState is
                                PetOperationUiState.Loading,
                        errorMessage = (
                                operationState as?
                                        PetOperationUiState.Error
                                )?.message,
                        onSave = { pet, photoUri ->
                            petViewModel.updatePet(pet, photoUri)
                        },
                        onCancel = {
                            navigationState.goBack()
                        }
                    )
                }

                detailUiState is PetDetailUiState.Error -> {
                    ErrorContent(
                        message = (
                                detailUiState as PetDetailUiState.Error
                                ).message,
                        onRetry = {
                            destination.petId?.let(
                                petDetailViewModel::loadPet
                            )
                        }
                    )
                }

                else -> {
                    LoadingContent()
                }
            }
        }
    }

    val entriesByTab = navigationState.backStacks.mapValues {
            (_, backStack) ->
        rememberDecoratedNavEntries(
            backStack = backStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator()
            ),
            entryProvider = destinationProvider
        )
    }

    val visibleEntries: List<NavEntry<NavKey>> =
        navigationState.visibleTabs.flatMap { tab ->
            entriesByTab.getValue(tab)
        }

    NavDisplay(
        entries = visibleEntries,
        onBack = navigationState::goBack,
        modifier = modifier
    )
}

@Composable
private fun PlaceholderScreen(
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = description,
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
