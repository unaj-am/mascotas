
package com.app.features.walkers.profile

data class WalkerProfileUiState(
    val name: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",

    val description: String = "",
    val experienceYears: String = "0",
    val serviceArea: String = "",
    val available: Boolean = false,
    val pricePerWalk: String = "0",
    val maxPets: String = "1",
    val rating: Double? = null,

    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isProfileCreated: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
