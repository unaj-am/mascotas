
package com.app.mascotas.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.features.pets.detail.PetDetailViewModel
import com.app.features.pets.list.PetViewModel
import com.app.mascotas.navigation.AppNavDisplay
import com.app.mascotas.navigation.NavigationState
import com.app.mascotas.navigation.TopLevelDestination

@Composable
fun AppShell(
    navigationState: NavigationState,
    ownerId: String,
    petViewModel: PetViewModel,
    petDetailViewModel: PetDetailViewModel,
    onPetClick: (String) -> Unit,
    onAddPetClick: () -> Unit
) {
    Scaffold(
        bottomBar = {
            AppBottomBar(
                currentTab = navigationState.currentTab,
                onTabClick = navigationState::selectTab
            )
        }
    ) { innerPadding ->
        AppNavDisplay(
            navigationState = navigationState,
            ownerId = ownerId,
            petViewModel = petViewModel,
            petDetailViewModel = petDetailViewModel,
            onPetClick = onPetClick,
            onAddPetClick = onAddPetClick,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun AppBottomBar(
    currentTab: TopLevelDestination,
    onTabClick: (TopLevelDestination) -> Unit
) {
    NavigationBar {
        TopLevelDestination.entries.forEach { tab ->
            NavigationBarItem(
                selected = currentTab == tab,
                onClick = {
                    onTabClick(tab)
                },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label
                    )
                },
                label = {
                    Text(tab.label)
                }
            )
        }
    }
}
