
package com.app.features.walkers.requests

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.app.core.ui.theme.MascotasThemeUI
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.app.core.ui.components.AdaptiveContent
import com.app.core.ui.components.AppCard
import com.app.core.ui.components.InfoRow
import com.app.core.ui.components.DomainErrorMessage
import com.app.core.ui.components.LoadingContent
import com.app.core.ui.components.SectionHeader
import com.app.core.ui.components.StatusBadge
import com.app.core.ui.components.TextActionButton
import com.app.core.ui.theme.DimensUI
import com.app.core.ui.theme.ShapeUI
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment

@Composable
fun WalkerRequestDetailScreen(
    state: WalkerRequestDetailUiState,
    onBack: () -> Unit = {}
) {
    AdaptiveContent(maxWidth = DimensUI.ContentMaxWidthDetail) {
        Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = DimensUI.ScreenHorizontalPadding,
                vertical = DimensUI.ScreenVerticalPadding
            ),
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SectionSpacing
        )
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                DimensUI.SpaceSmall
            )
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                SectionHeader(
                    title = "Detalle de solicitud",
                )
            }

            TextActionButton(
                text = "Volver",
                onClick = onBack
            )
        }

        when {
            state.isLoading -> {
                LoadingContent(
                    modifier = Modifier.fillMaxWidth(),
                    message = "Cargando solicitud..."
                )
            }

            state.errorMessage != null -> {
                DomainErrorMessage(message = state.errorMessage)
            }

            else -> {
                AppCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(
                            DimensUI.SpaceMedium
                        )
                    ) {
                        Text(
                            text = "Datos del paseo",
                            style = MaterialTheme.typography.titleMedium
                        )

                        InfoRow(
                            label = "Dueño",
                            value = state.ownerName.ifBlank {
                                "No disponible"
                            }
                        )

                        StatusBadge(
                            text = when (state.status) {
                                "PENDING" -> "Pendiente"
                                "ACCEPTED" -> "Aceptada"
                                "REJECTED" -> "Rechazada"
                                else -> state.status.ifBlank {
                                    "Sin estado"
                                }
                            }
                        )

                        InfoRow(
                            label = "Fecha y hora",
                            value = state.scheduledAt
                        )

                        InfoRow(
                            label = "Duración",
                            value = "${state.durationMinutes} minutos"
                        )

                        InfoRow(
                            label = "Observaciones",
                            value = state.observations.ifBlank {
                                "Sin observaciones"
                            }
                        )
                    }
                }

                Text(
                    text = "Mascotas",
                    style = MaterialTheme.typography.titleMedium
                )

                if (state.petNames.isEmpty()) {
                    Text(
                        text = "No hay mascotas disponibles para esta solicitud.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                state.petNames.forEachIndexed { index, petName ->
                    AppCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(
                                DimensUI.SpaceMedium
                            )
                        ) {
                            Text(
                                text = "Mascota ${index + 1}: $petName",
                                style = MaterialTheme.typography.titleSmall
                            )

                            val photoUrl =
                                state.petPhotos.getOrNull(index)

                            if (!photoUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = photoUrl,
                                    contentDescription = "Foto de $petName",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(ShapeUI.Card),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = "Sin foto disponible",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
        }
    }
}

@Preview(name = "Solicitud - Pendiente", showBackground = true, widthDp = 411, heightDp = 891)
@Composable
private fun WalkerRequestDetailScreenPreview() {
    WalkerRequestDetailPreviewContent(
        WalkerRequestDetailUiState(
            requestId = "REQUEST001",
            ownerName = "Franco Gómez",
            petNames = listOf("Luna", "Toby"),
            scheduledAt = "20/11/2026 a las 10:00",
            durationMinutes = 45,
            observations = "Llevar agua y evitar calles muy transitadas.",
            status = "PENDING"
        )
    )
}

@Preview(name = "Solicitud - Cargando", showBackground = true, widthDp = 411, heightDp = 891)
@Composable
private fun WalkerRequestDetailLoadingPreview() {
    WalkerRequestDetailPreviewContent(WalkerRequestDetailUiState(isLoading = true))
}

@Preview(name = "Solicitud - Error", showBackground = true, widthDp = 411, heightDp = 891)
@Composable
private fun WalkerRequestDetailErrorPreview() {
    WalkerRequestDetailPreviewContent(
        WalkerRequestDetailUiState(errorMessage = "No se pudo cargar la solicitud.")
    )
}

@Composable
private fun WalkerRequestDetailPreviewContent(state: WalkerRequestDetailUiState) {
    MascotasThemeUI(isWalker = true) {
        WalkerRequestDetailScreen(state = state, onBack = {})
    }
}

