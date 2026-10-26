
package com.app.mascotas.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import com.app.core.ui.theme.DimensUI

@Composable
fun AppShell(
    tabs: List<AppTab>,
    currentTab: NavKey,
    onTabClick: (NavKey) -> Unit,
    isWalker: Boolean = false,
    content: @Composable (PaddingValues) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.background,
        bottomBar = {
            NavigationBar(
                containerColor = colors.surface,
                contentColor = colors.onSurface,
                tonalElevation = DimensUI.SpaceXSmall,
                windowInsets = NavigationBarDefaults.windowInsets
            ) {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = currentTab == tab.key,
                        onClick = {
                            if (currentTab != tab.key) {
                                onTabClick(tab.key)
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label
                            )
                        },
                        label = {
                            Text(text = tab.label)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = colors.primary,
                            selectedTextColor = colors.primary,
                            indicatorColor = colors.primaryContainer,
                            unselectedIconColor = colors.onSurfaceVariant,
                            unselectedTextColor = colors.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}
