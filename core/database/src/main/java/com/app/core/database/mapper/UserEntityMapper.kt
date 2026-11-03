
package com.app.core.database.mapper

import com.app.core.database.model.UserEntity
import com.app.core.domain.model.User
import com.app.core.domain.model.UserRole

fun UserEntity.toDomain(): User {
    return User(
        id = id,
        name = name,
        lastName = lastName,
        email = email,
        phone = phone,
        profileImage = profileImage,
        role = UserRole.valueOf(role),
        active = active
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        lastName = lastName,
        email = email,
        phone = phone,
        profileImage = profileImage,
        role = role.name,
        active = active
    )
}
