package com.app.mascotas.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.app.features.profile.WalkerProfileScreen
import com.app.features.walkers.requests.WalkerRequestsScreen
import com.app.mascotas.ui.AppShell
import com.app.mascotas.ui.AppTab

@Composable
fun WalkerNavigation(
    userId: String,
    onSignOut: () -> Unit
) {
    val tabs = remember {
        listOf(
            AppTab(
                key = WalkerHomeDestination,
                label = "Inicio",
                icon = Icons.Filled.Home
            ),
            AppTab(
                key = WalkerAgendaDestination,
                label = "Agenda",
                icon = Icons.Filled.CalendarMonth
            ),
            AppTab(
                key = WalkerActiveWalkDestination,
                label = "Paseo activo",
                icon = Icons.Filled.DirectionsWalk
            ),
            AppTab(
                key = WalkerProfileDestination,
                label = "Perfil",
                icon = Icons.Filled.Person
            )
        )
    }

    val backStack = rememberNavBackStack(
        WalkerHomeDestination
    )

    val currentKey = backStack.last()

    val destinationProvider = entryProvider<NavKey> {
        entry<WalkerHomeDestination> {
            WalkerHomeScreen(
                onAgendaClick = {
                    navigateToRoot(
                        backStack,
                        WalkerAgendaDestination
                    )
                },
                onActiveWalkClick = {
                    navigateToRoot(
                        backStack,
                        WalkerActiveWalkDestination
                    )
                }
            )
        }

        entry<WalkerAgendaDestination> {
            WalkerAgendaScreen(
                onRequestsClick = {
                    backStack.add(WalkerRequestsDestination)
                }
            )
        }

        entry<WalkerActiveWalkDestination> {
            WalkerActiveWalkScreen()
        }

        entry<WalkerProfileDestination> {
            WalkerProfileScreen(
                userId = userId,
                onSignOut = onSignOut
            )
        }

        // Se conserva el flujo existente de solicitudes.
        entry<WalkerRequestsDestination> {
            WalkerRequestsScreen(
                walkerId = userId
            )
        }

        entry<WalkerHistoryDestination> {
            Text("El historial de paseos se implementará en una siguiente etapa.")
        }
    }

    val entries = rememberDecoratedNavEntries(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator()
        ),
        entryProvider = destinationProvider
    )

    BackHandler(enabled = backStack.size > 1) {
        backStack.removeAt(backStack.lastIndex)
    }

    AppShell(
        tabs = tabs,
        currentTab = currentKey,
        onTabClick = { selectedKey ->
            navigateToRoot(backStack, selectedKey)
        },
        isWalker = true
    ) { innerPadding ->
        NavDisplay(
            entries = entries,
            onBack = {
                if (backStack.size > 1) {
                    backStack.removeAt(backStack.lastIndex)
                } else {
                    navigateToRoot(
                        backStack,
                        WalkerHomeDestination
                    )
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}

private fun navigateToRoot(
    backStack: MutableList<NavKey>,
    destination: NavKey
) {
    backStack.clear()
    backStack.add(destination)
}

@Composable
private fun WalkerHomeScreen(
    onAgendaClick: () -> Unit,
    onActiveWalkClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Inicio",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Consultá tus próximos paseos y las actividades del día."
        )

        Text(
            text = "Los próximos paseos y las actividades se mostrarán " +
                    "cuando se integre la consulta de paseos asignados."
        )

        Button(onClick = onAgendaClick) {
            Text("Ver agenda")
        }

        Button(onClick = onActiveWalkClick) {
            Text("Ir al paseo activo")
        }
    }
}

@Composable
private fun WalkerAgendaScreen(
    onRequestsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Agenda",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Acá se mostrarán los paseos asignados, ordenados " +
                    "por fecha y horario."
        )

        Text(
            text = "La consulta de paseos confirmados todavía debe " +
                    "integrarse con los datos del módulo de paseos."
        )

        Button(onClick = onRequestsClick) {
            Text("Consultar solicitudes")
        }
    }
}

@Composable
private fun WalkerActiveWalkScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Paseo activo",
                style = MaterialTheme.typography.headlineLarge
            )

            Text(
                text = "Acá se mostrará el paseo en curso y el acceso " +
                        "a su seguimiento."
            )

            Text(
                text = "La detección del paseo activo y el mapa todavía " +
                        "deben conectarse con el módulo de paseos y ubicación."
            )
        }
    }
}
