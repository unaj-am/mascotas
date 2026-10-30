package com.app.features.walkers.list.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.domain.model.Walker
import com.app.core.ui.theme.DimensUI

@Composable
fun WalkerList(
    walkers: List<Walker>,
    onWalkerClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SpaceMedium
        )
    ) {
        items(
            items = walkers,
            key = { walker -> walker.userId }
        ) { walker ->
            WalkerCard(
                walker = walker,
                onClick = {
                    onWalkerClick(walker.userId)
                }
            )
        }
    }
}