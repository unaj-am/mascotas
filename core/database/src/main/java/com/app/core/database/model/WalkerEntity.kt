
package com.app.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "walkers")
data class WalkerEntity(

    @PrimaryKey
    val userId: String,

    val description: String,
    val experienceYears: Int,
    val serviceArea: String,
    val available: Boolean,
    val pricePerWalk: Double,
    val maxPets: Int,
    val rating: Double?
)
