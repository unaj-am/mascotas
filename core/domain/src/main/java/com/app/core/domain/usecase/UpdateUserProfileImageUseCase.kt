package com.app.core.domain.usecase

import com.app.core.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserProfileImageUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String, imageUrl: String) {
        require(userId.isNotBlank()) {
            "El identificador del usuario es obligatorio"
        }
        require(imageUrl.isNotBlank()) {
            "La URL de la imagen es obligatoria"
        }

        userRepository.updateProfileImage(userId, imageUrl)
    }
}
