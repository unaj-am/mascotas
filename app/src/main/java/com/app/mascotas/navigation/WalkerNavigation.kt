
package com.app.mascotas.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.app.features.home.walker.WalkerHomeRoute
import com.app.features.notifications.presentation.NotificationsRoute
import com.app.features.profile.WalkerProfileScreen
import com.app.features.walkers.requests.WalkerRequestsRoute
import com.app.features.walkers.requests.WalkerRequestDetailRoute
import com.app.features.walks.walker.WalkerActiveWalkRoute
import com.app.features.walks.walker.WalkerAgendaRoute
import com.app.features.walks.walker.WalkerHistoryRoute
import com.app.mascotas.ui.AppShell
import com.app.mascotas.ui.AppTab

@Composable
fun WalkerNavigation(
    userId: String,
    onSignOut: () -> Unit,
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit
) {
    val tabRoots: List<NavKey> = listOf(
        WalkerHomeDestination,
        WalkerRequestsDestination,
        WalkerAgendaDestination,
        WalkerProfileDestination
    )

    val tabs = remember {
        listOf(
            AppTab(WalkerHomeDestination, "Inicio", Icons.Filled.Home),
            AppTab(WalkerRequestsDestination, "Solicitudes", Icons.Filled.Inbox),
            AppTab(WalkerAgendaDestination, "Agenda", Icons.Filled.CalendarMonth),
            AppTab(WalkerProfileDestination, "Perfil", Icons.Filled.Person)
        )
    }

    var selectedTabIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    val backStacks: List<NavBackStack<NavKey>> = listOf(
        rememberNavBackStack(WalkerHomeDestination),
        rememberNavBackStack(WalkerRequestsDestination),
        rememberNavBackStack(WalkerAgendaDestination),
        rememberNavBackStack(WalkerProfileDestination)
    )

    val currentStack = backStacks[selectedTabIndex]
    val selectedTabRoot = tabRoots[selectedTabIndex]

    val destinationProvider = entryProvider<NavKey> {

        entry<WalkerHomeDestination> {
            WalkerHomeRoute(
                userId = userId,
                onNotificationsClick = {
                    navigateOnce(
                        currentStack,
                        NotificationsDestination
                    )
                }
            )
        }

        entry<NotificationsDestination> {
            NotificationsRoute(
                userId = userId
            )
        }

        entry<WalkerRequestsDestination> {
            WalkerRequestsRoute(
                walkerId = userId,
                onRequestClick = { requestId ->
                    navigateOnce(
                        currentStack,
                        WalkerRequestDetailDestination(requestId)
                    )
                }
            )
        }

        entry<WalkerRequestDetailDestination> { destination ->
            WalkerRequestDetailRoute(
                requestId = destination.requestId,
                onBack = {
                    if (
                        currentStack.size > 1 &&
                        currentStack.last() == destination
                    ) {
                        currentStack.removeAt(currentStack.lastIndex)
                    }
                }
            )
        }

        entry<WalkerAgendaDestination> {
            WalkerAgendaRoute(
                walkerId = userId,
                onActiveWalkClick = {
                    navigateOnce(
                        currentStack,
                        WalkerActiveWalkDestination
                    )
                },
                onHistoryClick = {
                    navigateOnce(
                        currentStack,
                        WalkerHistoryDestination
                    )
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
            WalkerProfileScreen(
                userId = userId,
                onSignOut = onSignOut,
                darkTheme = darkTheme,
                onDarkThemeChange = onDarkThemeChange
            )
        }
    }

    val entriesByTab = backStacks.map { backStack ->
        rememberDecoratedNavEntries(
            backStack = backStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator()
            ),
            entryProvider = destinationProvider
        )
    }

    val visibleTabIndices = if (selectedTabIndex == 0) {
        listOf(0)
    } else {
        listOf(0, selectedTabIndex)
    }

    val visibleEntries = visibleTabIndices.flatMap { index ->
        entriesByTab[index]
    }

    AppShell(
        tabs = tabs,
        currentTab = selectedTabRoot,
        onTabClick = { selectedKey ->
            val targetIndex = tabRoots.indexOf(selectedKey)

            if (targetIndex == selectedTabIndex) {
                popToRoot(backStacks[targetIndex])
            } else {
                popToRoot(backStacks[targetIndex])
                selectedTabIndex = targetIndex
            }
        },
        isWalker = true
    ) { innerPadding ->

        NavDisplay(
            entries = visibleEntries,
            onBack = {
                when {
                    currentStack.size > 1 -> {
                        currentStack.removeAt(currentStack.lastIndex)
                    }

                    selectedTabIndex != 0 -> {
                        selectedTabIndex = 0
                    }
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}

private fun navigateOnce(
    backStack: NavBackStack<NavKey>,
    destination: NavKey
) {
    if (backStack.last() != destination) {
        backStack.add(destination)
    }
}

private fun popToRoot(
    backStack: NavBackStack<NavKey>
) {
    while (backStack.size > 1) {
        backStack.removeAt(backStack.lastIndex)
    }
}
