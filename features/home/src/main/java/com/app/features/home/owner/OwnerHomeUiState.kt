
package com.app.features.home.owner

data class OwnerHomeUiState(
    val userName: String = "",
    val profileImage: String? = null,
    val petsCount: Int = 0,
    val upcomingWalksCount: Int = 0,
    val nextPetName: String? = null,
    val nextPetBreed: String? = null,
    val nextWalkDate: String? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
