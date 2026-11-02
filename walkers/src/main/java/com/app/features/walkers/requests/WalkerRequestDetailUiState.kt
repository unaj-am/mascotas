
package com.app.features.walkers.requests

data class WalkerRequestDetailUiState(
    val isLoading: Boolean = false,
    val requestId: String = "",
    val ownerName: String = "",
    val petNames: List<String> = emptyList(),
    val petPhotos: List<String> = emptyList(),
    val scheduledAt: String = "",
    val durationMinutes: Int = 0,
    val observations: String = "",
    val status: String = "",
    val errorMessage: String? = null
)
