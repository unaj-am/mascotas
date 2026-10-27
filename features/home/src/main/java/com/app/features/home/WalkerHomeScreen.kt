package com.app.features.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.core.ui.components.DomainErrorMessage
import com.app.core.ui.theme.DimensUI

@Composable
fun WalkerHomeRoute(
    userId: String,
    viewModel: WalkerHomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        viewModel.loadHome(userId)
    }

    WalkerHomeScreen(
        state = state,
        onAvailabilityChange = viewModel::onAvailabilityChange,
        onRetry = viewModel::retryLoadHome
    )
}

@Composable
private fun WalkerHomeScreen(
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
                    modifier = Modifier.padding(top = DimensUI.SpaceMedium)
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
                        style = MaterialTheme.typography.titleLarge
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
    }
}

@Composable
private fun WalkerHomeHeader(
    userName: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SpaceXSmall
        )
    ) {
        Text(
            text = "Hola, $userName",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Este es el resumen de tu actividad como paseador.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun WalkerAvailabilityCard(
    available: Boolean,
    isUpdating: Boolean,
    onAvailabilityChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DimensUI.SpaceMedium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(
                    DimensUI.SpaceXSmall
                )
            ) {
                Text(
                    text = "Mi disponibilidad",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = if (available) {
                        "Disponible para recibir solicitudes"
                    } else {
                        "No disponible para nuevos paseos"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (isUpdating) {
                    Text(
                        text = "Actualizando disponibilidad...",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Switch(
                checked = available,
                onCheckedChange = onAvailabilityChange,
                enabled = !isUpdating
            )
        }
    }
}

@Composable
private fun WalkerHomeStatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DimensUI.SpaceMedium),
            verticalArrangement = Arrangement.spacedBy(
                DimensUI.SpaceSmall
            )
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}