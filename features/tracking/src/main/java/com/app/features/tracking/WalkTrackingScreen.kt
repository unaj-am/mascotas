
package com.app.features.tracking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.core.domain.model.MatchedRoute
import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkLocation
import com.app.core.domain.model.WalkStatus
import com.app.core.ui.components.DomainErrorMessage
import com.app.core.ui.components.LoadingContent
import com.app.core.ui.components.SectionHeader
import com.app.features.tracking.component.WalkTrackingInfoCard
import com.app.features.tracking.component.WalkTrackingMap
import com.app.features.tracking.component.WalkTrackingStatusCard

@Composable
fun WalkTrackingScreen(
    walkId: String,
    onBack: () -> Unit,
    viewModel: WalkTrackingViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(walkId) {
        viewModel.load(
            walkId = walkId,
            forceRefresh = true
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionHeader(
            title = "Seguimiento del paseo",
            subtitle = "Consultá el estado y los datos del servicio.",
            actionText = "Volver",
            onAction = onBack
        )

        if (state.isLoading && state.walk == null) {
            LoadingContent(
                modifier = Modifier.weight(1f),
                message = "Consultando el paseo..."
            )
        } else {
            state.errorMessage?.let { message ->
                DomainErrorMessage(
                    message = message,
                    onRetry = viewModel::refresh
                )
            }

            val walk = state.walk

            if (walk != null) {
                WalkTrackingContent(
                    walk = walk,
                    currentLocation = state.currentLocation,
                    locationHistory = state.locationHistory,
                    matchedRoute = state.matchedRoute,
                    locationErrorMessage = state.locationErrorMessage,
                    historyErrorMessage = state.historyErrorMessage,
                    onRefresh = viewModel::refresh
                )
            } else if (state.errorMessage == null) {
                Text(
                    text = "No hay información disponible para este paseo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun WalkTrackingContent(
    walk: Walk,
    currentLocation: WalkLocation?,
    locationHistory: List<WalkLocation>,
    matchedRoute: MatchedRoute?,
    locationErrorMessage: String?,
    historyErrorMessage: String?,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (
            walk.status == WalkStatus.IN_PROGRESS ||
            walk.status == WalkStatus.COMPLETED ||
            walk.status == WalkStatus.CANCELLED
        ) {
            WalkTrackingMap(
                walkId = walk.id,
                currentLocation = currentLocation,
                locationHistory = locationHistory,
                matchedRoute = matchedRoute
            )
        }

        WalkTrackingInfoCard(
            walk = walk
        )

        WalkTrackingStatusCard(
            status = walk.status,
            currentLocation = currentLocation,
            locationErrorMessage = locationErrorMessage,
            historyErrorMessage = historyErrorMessage,
            onRefresh = onRefresh
        )
    }
}
