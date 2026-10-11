package com.app.core.domain.usecase

import com.app.core.domain.model.User
import com.app.core.domain.repository.UserRepository

class SaveUserUseCase(
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

        userRepository.saveUser(user)
    }
}