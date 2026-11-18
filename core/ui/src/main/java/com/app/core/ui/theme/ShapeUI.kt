package com.app.core.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Sistema de formas de Mascotas.
 *
 * Centraliza radios y formas utilizadas por los
 * componentes de la aplicación.
 */
val MascotasShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

/**
 * Aliases semánticos para componentes que requieren una forma específica.
 * Las formas estándar reutilizan [MascotasShapes] para evitar radios duplicados.
 */
object ShapeUI {
    val ExtraSmall: Shape get() = MascotasShapes.extraSmall
    val Small: Shape get() = MascotasShapes.small
    val Medium: Shape get() = MascotasShapes.medium
    val Large: Shape get() = MascotasShapes.large
    val ExtraLarge: Shape get() = MascotasShapes.extraLarge

    val Button: Shape get() = MascotasShapes.medium
    val ButtonPill: Shape = CircleShape

    val TextField: Shape get() = MascotasShapes.medium

    val Card: Shape get() = MascotasShapes.large
    val CardCompact: Shape get() = MascotasShapes.medium

    val Image: Shape get() = MascotasShapes.large
    val Avatar: Shape = CircleShape

    val Chip: Shape = CircleShape
    val Badge: Shape get() = MascotasShapes.extraSmall

    val Container: Shape = RoundedCornerShape(24.dp)
    val Dialog: Shape get() = MascotasShapes.extraLarge

    val BottomSheet: Shape = RoundedCornerShape(
        topStart = 28.dp,
        topEnd = 28.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )
}
