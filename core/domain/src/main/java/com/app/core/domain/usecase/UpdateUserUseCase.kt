
package com.app.core.domain.usecase

import com.app.core.domain.model.User
import com.app.core.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: User) {
        require(user.id.isNotBlank()) {
            "El identificador del usuario es obligatorio"
        }
        require(user.name.isNotBlank()) {
            "El nombre del usuario es obligatorio"
        }
        require(user.lastName.isNotBlank()) {
            "El apellido del usuario es obligatorio"
        }
        require(user.email.isNotBlank()) {
            "El correo electrónico es obligatorio"
        }

        val existingUser = userRepository.getUserById(user.id)

        require(existingUser != null) {
            "No existe un usuario con ese identificador"
        }

        userRepository.updateUser(user)
    }
}
