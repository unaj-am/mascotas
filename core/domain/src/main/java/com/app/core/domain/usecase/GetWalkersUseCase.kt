package com.app.core.domain.usecase

import com.app.core.domain.repository.WalkerRepository
import javax.inject.Inject

class GetWalkersUseCase @Inject constructor(
    private val walkerRepository: WalkerRepository
) {
    suspend operator fun invoke() =
        walkerRepository.getWalkers()
}