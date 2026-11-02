
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
import com.app.features.home.component.PendingRequestPreviewCard
import com.app.features.home.component.WalkerAvailabilityCard
import com.app.features.home.component.WalkerHomeHeader
import com.app.features.home.component.WalkerHomeStatCard

@Composable
fun WalkerHomeScreen(
    state: WalkerHomeUiState,
    onAvailabilityChange: (Boolean) -> Unit,
    onRetry: () -> Unit,
    onNotificationsClick: (() -> Unit)? = null,
    notificationCount: Int = 0
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
                modifier = Modifier.fillMaxSize(),
                onRetry = onRetry
            )
        }

        else -> {
            WalkerHomeContent(
                state = state,
                onAvailabilityChange = onAvailabilityChange,
                onRetry = onRetry,
                onNotificationsClick = onNotificationsClick,
                notificationCount = notificationCount
            )
        }
    }
}

@Composable
private fun WalkerHomeContent(
    state: WalkerHomeUiState,
    onAvailabilityChange: (Boolean) -> Unit,
    onRetry: () -> Unit,
    onNotificationsClick: (() -> Unit)?,
    notificationCount: Int
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

        // 1. Encabezado del paseador
        item {
            AppAnimatedVisibility(
                visible = true,
                durationMillis = 260
            ) {
                WalkerHomeHeader(
                    userName = state.userName,
                    profileImage = state.profileImage,
                    onNotificationsClick = onNotificationsClick,
                    notificationCount = notificationCount
                )
            }
        }

        // 2. Disponibilidad
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

        // 3. Resumen de actividad
        item {
            AppAnimatedVisibility(
                visible = true,
                durationMillis = 300
            ) {
                SectionHeader(
                    title = "Resumen de actividad",
                    subtitle = "Consultá el estado de tus paseos y solicitudes"
                )
            }
        }

        // Primera fila de estadísticas
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

        // Segunda fila de estadísticas
        item {
            AppAnimatedVisibility(
                visible = true,
                durationMillis = 340
            ) {
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
                        value = state.rating?.let {
                            "%.1f / 5".format(it)
                        } ?: "Sin calificar",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 4. Solicitudes pendientes
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
                        message = "Cuando recibas nuevas solicitudes de paseo, aparecerán acá.",
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

        // 5. Error recuperable
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

        // Confirmación de actualización de disponibilidad
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
