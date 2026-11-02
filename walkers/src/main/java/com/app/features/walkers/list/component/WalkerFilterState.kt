package com.app.features.walkers.list.component

import com.app.core.domain.model.Walker
import com.app.features.walkers.list.WalkerUiState

data class WalkerFilterState(
    val serviceArea: String = "",
    val maxPriceText: String = "",
    val onlyAvailable: Boolean = false
) {

    val maxPrice: Double?
        get() = maxPriceText
            .trim()
            .replace(",", ".")
            .toDoubleOrNull()

    val isPriceInvalid: Boolean
        get() = maxPriceText.isNotBlank() && maxPrice == null

    val hasActiveFilters: Boolean
        get() = serviceArea.isNotBlank() ||
                maxPriceText.isNotBlank() ||
                onlyAvailable

    val activeFilterCount: Int
        get() = listOf(
            serviceArea.isNotBlank(),
            maxPriceText.isNotBlank(),
            onlyAvailable
        ).count { it }

    fun clear(): WalkerFilterState = WalkerFilterState()

    fun filterWalkers(
        walkers: List<Walker>
    ): List<Walker> {
        val priceLimit = maxPrice

        return walkers.filter { walker ->
            val matchesArea =
                serviceArea.isBlank() ||
                        walker.serviceArea.contains(
                            serviceArea.trim(),
                            ignoreCase = true
                        )

            val matchesPrice =
                priceLimit == null ||
                        walker.pricePerWalk <= priceLimit

            val matchesAvailability =
                !onlyAvailable || walker.available

            matchesArea && matchesPrice && matchesAvailability
        }
    }

    fun applyTo(uiState: WalkerUiState): WalkerUiState {
        return when (uiState) {
            is WalkerUiState.Success -> {
                WalkerUiState.Success(
                    walkers = filterWalkers(uiState.walkers)
                )
            }

            WalkerUiState.Loading ->
                WalkerUiState.Loading

            is WalkerUiState.Error ->
                uiState
        }
    }
}