
package com.app.mascotas.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.app.features.home.OwnerHomeScreen
import com.app.features.pets.list.PetRoute
import com.app.features.pets.list.PetViewModel

@Composable
fun AppNavDisplay(
    navigationState: NavigationState,
    ownerId: String,
    petViewModel: PetViewModel,
    onPetClick: (String) -> Unit,
    onAddPetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val destinationProvider = entryProvider<NavKey> {

        entry<HomeDestination> {
            OwnerHomeScreen(
                userName = "Dueño",
                petsCount = 0,
                upcomingWalksCount = 0,
                nextPetName = null,
                nextPetBreed = null,
                nextWalkDate = null,
                onNotificationsClick = {},
                onPetsClick = {
                    navigationState.selectTab(
                        TopLevelDestination.PETS
                    )
                },
                onWalksClick = {
                    navigationState.selectTab(
                        TopLevelDestination.WALKS
                    )
                },
                onFindWalkerClick = {
                    navigationState.selectTab(
                        TopLevelDestination.WALKS
                    )
                },
                onHistoryClick = {
                    navigationState.selectTab(
                        TopLevelDestination.WALKS
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
            PlaceholderScreen(
                title = "Mis paseos",
                description =
                    "El módulo de paseos se integrará en este apartado."
            )
        }

        entry<ProfileDestination> {
            PlaceholderScreen(
                title = "Mi perfil",
                description =
                    "El módulo de perfil se integrará en este apartado."
            )
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
