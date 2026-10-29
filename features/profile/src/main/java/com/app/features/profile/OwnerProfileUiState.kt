
package com.app.features.profile

data class OwnerProfileUiState(
    val userId: String = "",
    val name: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",

    val isLoading: Boolean = true,
    val isSaving: Boolean = false,

    val errorMessage: String? = null,
    val successMessage: String? = null,

    // Foto de perfil (Cloudinary)
    val profileImage: String? = null,
    val isUploadingPhoto: Boolean = false,
    val photoErrorMessage: String? = null
)