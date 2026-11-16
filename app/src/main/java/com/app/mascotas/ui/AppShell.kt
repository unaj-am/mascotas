package com.app.mascotas.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold

@Composable
fun AppShell(
    tabs: List<AppTab>,
    currentTab: NavKey,
    onTabClick: (NavKey) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val contentInsets = WindowInsets.safeDrawing
        .only(
            WindowInsetsSides.Top +
                WindowInsetsSides.Start +
                WindowInsetsSides.End
        )
        .asPaddingValues()

    NavigationSuiteScaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        navigationSuiteItems = {
            tabs.forEach { tab ->
                item(
                    selected = currentTab == tab.key,
                    onClick = { onTabClick(tab.key) },
                    icon = {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label
                        )
                    },
                    label = { Text(text = tab.label) },
                    alwaysShowLabel = true
                )
            }
        }
    ) {
        content(contentInsets)
    }
}
