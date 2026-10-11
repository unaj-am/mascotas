package com.app.core.domain.model

data class Pet(
    val id: String,
    val ownerId: String,
    val name: String,
    val breed: String,
    val age: Int,
    val weight: Double,
    val observations: String = ""
)