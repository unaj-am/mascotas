
package com.app.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(

    @PrimaryKey
    val id: String,

    val name: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val profileImage: String?,
    val role: String,
    val active: Boolean
)
