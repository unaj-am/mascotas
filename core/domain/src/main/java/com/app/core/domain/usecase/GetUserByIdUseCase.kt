
package com.app.core.domain.usecase

import com.app.core.domain.model.User
import com.app.core.domain.repository.UserRepository
import javax.inject.Inject

class GetUserByIdUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): User? {
        require(userId.isNotBlank()) {
            "El identificador del usuario no puede estar vacío"
        }

        return userRepository.getUserById(userId)
    }
}
