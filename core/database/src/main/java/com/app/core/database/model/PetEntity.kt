package com.app.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pets")
data class PetEntity(
    @PrimaryKey
    val id: String,

    val ownerId: String,
    val name: String,
    val breed: String,
    val age: Int,
    val weight: Double,
    val observations: String,
    val photoUri: String?,
    val size: String,
    val behavior: String,
    val specialNeeds: String
)
