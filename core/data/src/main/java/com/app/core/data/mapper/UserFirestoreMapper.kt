package com.app.core.data.mapper

import com.app.core.domain.model.User
import com.app.core.domain.model.UserRole
import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.toUser(): User? {
    val role = getString("role")
        ?.let { roleName -> runCatching { UserRole.valueOf(roleName) }.getOrNull() }
        ?: return null

    return User(
        id = id,
        name = getString("name").orEmpty(),
        lastName = getString("lastName").orEmpty(),
        email = getString("email").orEmpty(),
        phone = getString("phone").orEmpty(),
        profileImage = getString("profileImage"),
        role = role,
        active = getBoolean("active") ?: true
    )
}

fun User.toFirestoreMap(): Map<String, Any?> =
    mapOf(
        "name" to name,
        "lastName" to lastName,
        "email" to email,
        "phone" to phone,
        "profileImage" to profileImage,
        "role" to role.name,
        "active" to active
    )
