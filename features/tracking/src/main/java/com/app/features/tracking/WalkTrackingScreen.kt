package com.app.features.tracking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.core.domain.model.WalkStatus
import com.app.core.ui.components.DomainErrorMessage
import com.app.core.ui.components.LoadingContent
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun WalkTrackingScreen(
    walkId: String,
    onBack: () -> Unit,
    viewModel: WalkTrackingViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(walkId) { viewModel.load(walkId, forceRefresh = true) }
    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Seguimiento del paseo", style = MaterialTheme.typography.headlineMedium)
            TextButton(onClick = onBack) { Text("Volver") }
        }
        if (state.isLoading && state.walk == null) LoadingContent(Modifier.weight(1f))
        else if (state.errorMessage != null) DomainErrorMessage(state.errorMessage.orEmpty(), onRetry = viewModel::refresh)
        else state.walk?.let { walk ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(statusLabel(walk.status), style = MaterialTheme.typography.titleLarge)
                    Text("Fecha programada: ${formatDate(walk.scheduledAt)}")
                    Text("Duración prevista: ${walk.durationMinutes} minutos")
                    Text("Mascotas: ${walk.petIds.size}")
                    walk.startedAt?.let { Text("Inicio: ${formatDate(it)}") }
                    walk.finishedAt?.let { Text("Finalización: ${formatDate(it)}") }
                    if (walk.observations.isNotBlank()) Text("Observaciones: ${walk.observations}")
                }
            }
            if (walk.status == WalkStatus.IN_PROGRESS) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                    Text(
                        text = "El paseo está en curso. El mapa y la ubicación en tiempo real todavía no están conectados; esta pantalla muestra el estado confirmado del servicio.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                TextButton(onClick = viewModel::refresh) { Text("Actualizar estado") }
            }
        }
    }
}

private fun statusLabel(status: WalkStatus): String = when (status) {
    WalkStatus.SCHEDULED -> "Paseo confirmado"
    WalkStatus.IN_PROGRESS -> "Paseo en curso"
    WalkStatus.COMPLETED -> "Paseo finalizado"
    WalkStatus.CANCELLED -> "Paseo cancelado"
}

private fun formatDate(value: String): String = runCatching {
    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale("es", "AR"))
        .withZone(ZoneId.systemDefault()).format(Instant.parse(value))
}.getOrDefault(value)
