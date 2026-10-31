package com.app.features.home.walker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.core.ui.components.AppAnimatedVisibility
import com.app.core.ui.components.AppFeedbackType
import com.app.core.ui.components.AppInlineFeedback
import com.app.core.ui.components.DomainErrorMessage
import com.app.core.ui.components.EmptyContent
import com.app.core.ui.components.LoadingContent
import com.app.core.ui.components.SectionHeader
import com.app.core.ui.theme.DimensUI
import com.app.features.home.component.WalkerAvailabilityCard
import com.app.features.home.component.WalkerHomeHeader
import com.app.features.home.component.WalkerHomeStatCard
import com.app.features.home.component.PendingRequestPreviewCard
@Composable
fun WalkerHomeScreen(
    state: WalkerHomeUiState,
    onAvailabilityChange: (Boolean) -> Unit,
    onRetry: () -> Unit
) {
    when {
        state.isLoading -> {
            LoadingContent(
                message = "Cargando inicio..."
            )
        }

        state.errorMessage != null &&
                state.userName.isBlank() -> {
            DomainErrorMessage(
                message = state.errorMessage,
                modifier = Modifier
                    .fillMaxSize()
            )
            // El reintento se mantiene disponible.
            // Se presenta mediante el contenedor de error.
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
            AppAnimatedVisibility(
                visible = true,
                durationMillis = 260
            ) {
                WalkerHomeHeader(
                    userName = state.userName
                )
            }
        }

        item {
            AppAnimatedVisibility(
                visible = true,
                durationMillis = 280
            ) {
                WalkerAvailabilityCard(
                    available = state.available,
                    isUpdating = state.isUpdatingAvailability,
                    onAvailabilityChange = onAvailabilityChange
                )
            }
        }

        item {
            AppAnimatedVisibility(
                visible = true,
                durationMillis = 300
            ) {
                SectionHeader(
                    title = "Resumen de actividad",
                    subtitle = "Tu actividad como paseador"
                )
            }
        }

        item {
            AppAnimatedVisibility(
                visible = true,
                durationMillis = 320
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        DimensUI.SpaceMedium
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
        }

        item {
            AppAnimatedVisibility(
                visible = true,
                durationMillis = 340
            ) {
                if (state.rating != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            DimensUI.SpaceMedium
                        )
                    ) {
                        WalkerHomeStatCard(
                            title = "Paseos en curso",
                            value = state.activeWalks.toString(),
                            modifier = Modifier.weight(1f)
                        )

                        WalkerHomeStatCard(
                            title = "Calificación promedio",
                            value = "%.1f / 5".format(state.rating),
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    WalkerHomeStatCard(
                        title = "Paseos en curso",
                        value = state.activeWalks.toString(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        item {
            AppAnimatedVisibility(
                visible = true,
                durationMillis = 360
            ) {
                SectionHeader(
                    title = "Solicitudes pendientes",
                    subtitle = if (state.pendingRequestItems.isEmpty()) {
                        "No tenés solicitudes nuevas"
                    } else {
                        "${state.pendingRequestItems.size} solicitudes para revisar"
                    }
                )
            }
        }

        if (state.pendingRequestItems.isEmpty()) {
            item {
                AppAnimatedVisibility(
                    visible = true,
                    durationMillis = 380
                ) {
                    EmptyContent(
                        title = "Todo al día",
                        message = "Cuando recibas nuevas solicitudes, aparecerán en esta sección.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 140.dp)
                    )
                }
            }
        } else {
            items(
                items = state.pendingRequestItems,
                key = { request -> request.id }
            ) { request ->
                AppAnimatedVisibility(
                    visible = true,
                    durationMillis = 380
                ) {
                    PendingRequestPreviewCard(
                        request = request
                    )
                }
            }
        }

        if (state.errorMessage != null) {
            item {
                AppAnimatedVisibility(
                    visible = true,
                    durationMillis = 220
                ) {
                    DomainErrorMessage(
                        message = state.errorMessage,
                        onRetry = onRetry
                    )
                }
            }
        }

        state.availabilityMessage?.let { message ->
            item {
                AppAnimatedVisibility(
                    visible = true,
                    durationMillis = 220
                ) {
                    AppInlineFeedback(
                        message = message,
                        type = AppFeedbackType.SUCCESS
                    )
                }
            }
        }
    }
}
