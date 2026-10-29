package com.app.features.walkers.requestform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun WalkRequestFormRoute(
    ownerId: String,
    walkerId: String,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: WalkRequestFormViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    LaunchedEffect(ownerId) {
        viewModel.loadPets(ownerId)
    }

    WalkRequestFormScreen(
        uiState = uiState,
        onPetSelected = viewModel::togglePet,
        onScheduledAtChange = viewModel::updateScheduledAt,
        onDurationChange = viewModel::updateDuration,
        onObservationsChange = viewModel::updateObservations,
        onSubmit = {
            viewModel.submitRequest(
                ownerId = ownerId,
                walkerId = walkerId
            )
        },
        onBack = onBack,
        onSuccess = {
            viewModel.consumeSuccess()
            onSuccess()
        }
    )
}