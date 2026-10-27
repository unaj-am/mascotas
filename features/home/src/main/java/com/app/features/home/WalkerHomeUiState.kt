package com.app.features.home

data class WalkerHomeUiState(
    val userName: String = "",
    val available: Boolean = false,
    val pendingRequests: Int = 0,
    val scheduledWalks: Int = 0,
    val activeWalks: Int = 0,
    val rating: Double? = null,
    val isLoading: Boolean = true,
    val isUpdatingAvailability: Boolean = false,
    val errorMessage: String? = null,
    val availabilityMessage: String? = null
)
