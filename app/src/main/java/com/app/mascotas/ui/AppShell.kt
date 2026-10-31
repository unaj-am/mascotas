
package com.app.mascotas.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import com.app.core.ui.components.AppBottomNavigation
import com.app.core.ui.components.AppBottomNavigationItem

@Suppress("UNUSED_PARAMETER")
@Composable
fun AppShell(
    tabs: List<AppTab>,
    currentTab: NavKey,
    onTabClick: (NavKey) -> Unit,
    isWalker: Boolean = false,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            AppBottomNavigation(
                items = tabs.map { tab ->
                    AppBottomNavigationItem(
                        label = tab.label,
                        selected = currentTab == tab.key,
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label
                            )
                        },
                        onClick = {
                            onTabClick(tab.key)
                        }
                    )
                }
            )
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}
