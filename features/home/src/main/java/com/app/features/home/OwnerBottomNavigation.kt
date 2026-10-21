
package com.app.features.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.ui.theme.PetOwnerPurple

enum class OwnerTab {
    HOME,
    PETS,
    WALKS,
    PROFILE
}

@Composable
fun OwnerBottomNavigation(
    selectedTab: OwnerTab,
    onTabSelected: (OwnerTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        Triple(OwnerTab.HOME, "Inicio", Icons.Filled.Home),
        Triple(OwnerTab.PETS, "Mascotas", Icons.Filled.Pets),
        Triple(OwnerTab.WALKS, "Paseos", Icons.Filled.DirectionsWalk),
        Triple(OwnerTab.PROFILE, "Perfil", Icons.Filled.Person)
    )

    NavigationBar(modifier = modifier) {
        tabs.forEach { (tab, label, icon) ->
            NavigationBarItem(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label
                    )
                },
                label = { Text(label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PetOwnerPurple,
                    selectedTextColor = PetOwnerPurple,
                    indicatorColor = PetOwnerPurple.copy(alpha = 0.12f)
                )
            )
        }
    }
}
