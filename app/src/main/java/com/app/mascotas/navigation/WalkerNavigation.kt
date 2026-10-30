package com.app.mascotas.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.app.features.home.walker.WalkerHomeRoute
import com.app.features.profile.WalkerProfileScreen
import com.app.mascotas.ui.AppShell
import com.app.mascotas.ui.AppTab
import com.app.features.walkers.requests.WalkerRequestsRoute
import com.app.features.walks.walker.WalkerActiveWalkRoute
import com.app.features.walks.walker.WalkerAgendaRoute
import com.app.features.walks.walker.WalkerHistoryRoute
@Composable
fun WalkerNavigation(
    userId: String,
    onSignOut: () -> Unit
) {
    val tabs = remember {
        listOf(
            AppTab(WalkerHomeDestination, "Inicio", Icons.Filled.Home),
            AppTab(WalkerRequestsDestination, "Solicitudes", Icons.Filled.Inbox),
            AppTab(WalkerAgendaDestination, "Agenda", Icons.Filled.CalendarMonth),
            AppTab(WalkerProfileDestination, "Perfil", Icons.Filled.Person)
        )
    }

    val backStack = rememberNavBackStack(WalkerHomeDestination)
    val currentKey = backStack.last()
    val selectedTab = when (currentKey) {
        WalkerActiveWalkDestination, WalkerHistoryDestination -> WalkerAgendaDestination
        else -> currentKey
    }

    val destinationProvider = entryProvider<NavKey> {
        entry<WalkerHomeDestination> {
            WalkerHomeRoute(userId = userId)
        }
        entry<WalkerRequestsDestination> {
            WalkerRequestsRoute(walkerId = userId)
        }
        entry<WalkerAgendaDestination> {
            WalkerAgendaRoute(
                walkerId = userId,
                onActiveWalkClick = {
                    backStack.add(WalkerActiveWalkDestination)
                },
                onHistoryClick = {
                    backStack.add(WalkerHistoryDestination)
                }
            )
        }

        entry<WalkerActiveWalkDestination> {
            WalkerActiveWalkRoute(
                walkerId = userId
            )
        }

        entry<WalkerHistoryDestination> {
            WalkerHistoryRoute(
                walkerId = userId
            )
        }
        entry<WalkerProfileDestination> {
            WalkerProfileScreen(userId = userId, onSignOut = onSignOut)
        }
    }

    val entries = rememberDecoratedNavEntries(
        backStack = backStack,
        entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator()),
        entryProvider = destinationProvider
    )

    BackHandler(enabled = backStack.size > 1) {
        backStack.removeAt(backStack.lastIndex)
    }

    AppShell(
        tabs = tabs,
        currentTab = selectedTab,
        onTabClick = { selectedKey -> navigateToRoot(backStack, selectedKey) },
        isWalker = true
    ) { innerPadding ->
        NavDisplay(
            entries = entries,
            onBack = {
                if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
                else navigateToRoot(backStack, WalkerHomeDestination)
            },
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        )
    }
}

private fun navigateToRoot(backStack: MutableList<NavKey>, destination: NavKey) {
    backStack.clear()
    backStack.add(destination)
}
