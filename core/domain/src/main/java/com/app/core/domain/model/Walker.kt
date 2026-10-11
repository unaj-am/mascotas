package com.app.core.domain.model

data class Walker(
    val userId: String,
    val description: String,
    val experienceYears: Int,
    val serviceArea: String,
    val available: Boolean = false,
    val pricePerWalk: Double = 0.0,
    val maxPets: Int = 1,
    val rating: Double? = null
)