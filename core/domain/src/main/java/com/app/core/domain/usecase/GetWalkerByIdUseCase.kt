package com.app.core.domain.usecase

import com.app.core.domain.model.Walker
import com.app.core.domain.repository.WalkerRepository

class GetWalkerByIdUseCase(
    private val walkerRepository: WalkerRepository
) {
    suspend operator fun invoke(userId: String): Walker? {
        require(userId.isNotBlank()) {
            "El identificador del usuario no puede estar vacío"
        }

        return walkerRepository.getWalkerById(userId)
    }
}