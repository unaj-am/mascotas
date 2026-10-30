package com.app.features.walkers.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.app.core.ui.theme.DimensUI
import com.app.features.walkers.list.component.WalkerContent
import com.app.features.walkers.list.component.WalkerHeader
import com.app.features.walkers.list.component.WalkerFilterSection
import com.app.features.walkers.list.component.WalkerFilterState

/**
 * Route del listado de paseadores.
 */
@Composable
fun WalkerScreen(
    uiState: WalkerUiState,
    onWalkerClick: (String) -> Unit = {},
    onRetry: () -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var serviceArea by rememberSaveable {
        mutableStateOf("")
    }

    var maxPriceText by rememberSaveable {
        mutableStateOf("")
    }

    var onlyAvailable by rememberSaveable {
        mutableStateOf(false)
    }

    val filterState = WalkerFilterState(
        serviceArea = serviceArea,
        maxPriceText = maxPriceText,
        onlyAvailable = onlyAvailable
    )

    val displayState = filterState.applyTo(uiState)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = DimensUI.ScreenHorizontalPadding,
                vertical = DimensUI.ScreenVerticalPadding
            ),
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SectionSpacing
        )
    ) {
        WalkerHeader(
            uiState = displayState
        )

        if (uiState is WalkerUiState.Success) {
            val resultCount =
                (displayState as? WalkerUiState.Success)
                    ?.walkers
                    ?.size
                    ?: 0

            WalkerFilterSection(
                state = filterState,
                resultCount = resultCount,
                onStateChange = { newState ->
                    serviceArea = newState.serviceArea
                    maxPriceText = newState.maxPriceText
                    onlyAvailable = newState.onlyAvailable
                }
            )
        }

        Box(
            modifier = Modifier.weight(1f)
        ) {
            WalkerContent(
                uiState = displayState,
                onWalkerClick = onWalkerClick,
                onRetry = onRetry,
                onBack = onBack,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}