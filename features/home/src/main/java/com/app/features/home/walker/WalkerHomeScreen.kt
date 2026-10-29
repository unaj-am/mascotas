package com.app.features.home.walker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.core.domain.model.WalkRequest
import com.app.core.ui.components.AppCard
import com.app.core.ui.components.DomainErrorMessage
import com.app.core.ui.theme.DimensUI
import com.app.features.home.component.WalkerAvailabilityCard
import com.app.features.home.component.WalkerHomeHeader
import com.app.features.home.component.WalkerHomeStatCard

@Composable
fun WalkerHomeScreen(
    state: WalkerHomeUiState,
    onAvailabilityChange: (Boolean) -> Unit,
    onRetry: () -> Unit
) {
    when {
        state.isLoading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()

                Text(
                    text = "Cargando inicio...",
                    modifier = Modifier.padding(
                        top = DimensUI.SpaceMedium
                    )
                )
            }
        }

        state.errorMessage != null &&
                state.userName.isBlank() -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(DimensUI.ScreenHorizontalPadding),
                verticalArrangement = Arrangement.spacedBy(
                    DimensUI.SpaceLarge
                )
            ) {
                Text(
                    text = "Inicio",
                    style = MaterialTheme.typography.headlineLarge
                )

                DomainErrorMessage(
                    message = state.errorMessage,
                    onRetry = onRetry
                )
            }
        }

        else -> {
            WalkerHomeContent(
                state = state,
                onAvailabilityChange = onAvailabilityChange,
                onRetry = onRetry
            )
        }
    }
}

@Composable
private fun WalkerHomeContent(
    state: WalkerHomeUiState,
    onAvailabilityChange: (Boolean) -> Unit,
    onRetry: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = DimensUI.ScreenHorizontalPadding,
            vertical = DimensUI.ScreenVerticalPadding
        ),
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SectionSpacing
        )
    ) {
        item {
            WalkerHomeHeader(
                userName = state.userName
            )
        }

        item {
            WalkerAvailabilityCard(
                available = state.available,
                isUpdating = state.isUpdatingAvailability,
                onAvailabilityChange = onAvailabilityChange
            )
        }

        item {
            Text(
                text = "Resumen de actividad",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    DimensUI.SpaceSmall
                )
            ) {
                WalkerHomeStatCard(
                    title = "Solicitudes pendientes",
                    value = state.pendingRequests.toString(),
                    modifier = Modifier.weight(1f)
                )

                WalkerHomeStatCard(
                    title = "Paseos programados",
                    value = state.scheduledWalks.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            WalkerHomeStatCard(
                title = "Paseos en curso",
                value = state.activeWalks.toString(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        state.rating?.let { rating ->
            item {
                WalkerHomeStatCard(
                    title = "Calificación promedio",
                    value = "%.1f / 5".format(rating),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item {
            Text(
                text = "Solicitudes pendientes",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (state.pendingRequestItems.isEmpty()) {
            item {
                Text(
                    text = "No tenés solicitudes pendientes.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        } else {
            items(
                items = state.pendingRequestItems,
                key = { request -> request.id }
            ) { request ->
                PendingRequestPreviewCard(
                    request = request
                )
            }
        }

        if (state.errorMessage != null) {
            item {
                DomainErrorMessage(
                    message = state.errorMessage,
                    onRetry = onRetry
                )
            }
        }

        state.availabilityMessage?.let { message ->
            item {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun PendingRequestPreviewCard(
    request: WalkRequest
) {
    AppCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(DimensUI.SpaceLarge),
            verticalArrangement = Arrangement.spacedBy(
                DimensUI.SpaceSmall
            )
        ) {
            Text(
                text = "Nueva solicitud",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Fecha y hora: ${request.scheduledAt}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Duración: ${request.durationMinutes} minutos",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            val petCount = request.petIds.size

            Text(
                text = if (petCount == 1) {
                    "1 mascota"
                } else {
                    "$petCount mascotas"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (request.observations.isNotBlank()) {
                Text(
                    text = "Observaciones: ${request.observations}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "Pendiente de revisión",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
