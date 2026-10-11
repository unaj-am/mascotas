package com.app.core.domain.usecase

import com.app.core.domain.model.Walker
import com.app.core.domain.repository.WalkerRepository

class GetAvailableWalkersUseCase(
    private val walkerRepository: WalkerRepository
) {
    suspend operator fun invoke(): List<Walker> {
        return walkerRepository.getAvailableWalkers()
    }
}