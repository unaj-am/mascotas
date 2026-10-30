package com.app.features.walks.owner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.core.ui.components.DomainErrorMessage
import com.app.core.ui.components.LoadingContent
import com.app.features.walks.owner.component.OwnerWalkDetailContent
import com.app.features.walks.owner.component.OwnerWalkDetailHeader

@Composable
fun OwnerWalkDetailScreen(
    state: OwnerWalkDetailUiState,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onTrackWalk: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OwnerWalkDetailHeader(
            onBack = onBack
        )

        when {
            state.isLoading && state.walk == null -> {
                LoadingContent(
                    modifier = Modifier.weight(1f),
                    message = "Cargando detalle del paseo..."
                )
            }

            state.errorMessage != null && state.walk == null -> {
                DomainErrorMessage(
                    message = state.errorMessage.orEmpty(),
                    onRetry = onRefresh
                )
            }

            state.walk != null -> {
                OwnerWalkDetailContent(
                    walk = state.walk,
                    walkerName = state.walkerName,
                    onTrackWalk = onTrackWalk,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}