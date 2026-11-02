package com.app.features.walkers.requestform

import com.app.core.domain.model.Pet

data class WalkRequestFormUiState(
    val pets: List<Pet> = emptyList(),
    val selectedPetIds: Set<String> = emptySet(),
    val scheduledAt: String = "",
    val durationMinutes: String = "60",
    val observations: String = "",
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)