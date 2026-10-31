
package com.app.core.ui.theme

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

// =====================================================
// FORMAS MATERIAL 3
// =====================================================

val MascotasShapes = Shapes(

    // Etiquetas, indicadores y elementos pequeños
    extraSmall = RoundedCornerShape(8.dp),

    // Campos compactos y chips
    small = RoundedCornerShape(12.dp),

    // Botones y campos de entrada
    medium = RoundedCornerShape(16.dp),

    // Tarjetas y contenedores
    large = RoundedCornerShape(20.dp),

    // Secciones destacadas y modales
    extraLarge = RoundedCornerShape(28.dp)
)

// =====================================================
// FORMAS PERSONALIZADAS
// =====================================================

object ShapeUI {

    // Elementos pequeños
    val ExtraSmall: Shape = RoundedCornerShape(8.dp)

    // Componentes compactos
    val Small: Shape = RoundedCornerShape(12.dp)

    // Componentes estándar
    val Medium: Shape = RoundedCornerShape(16.dp)

    // Tarjetas
    val Large: Shape = RoundedCornerShape(20.dp)

    // Contenedores destacados
    val ExtraLarge: Shape = RoundedCornerShape(28.dp)

    // Botones
    val Button: Shape = RoundedCornerShape(16.dp)

    val ButtonPill: Shape = RoundedCornerShape(50)

    // Campos de texto
    val TextField: Shape = RoundedCornerShape(16.dp)

    // Tarjetas
    val Card: Shape = RoundedCornerShape(20.dp)

    val CardCompact: Shape = RoundedCornerShape(16.dp)

    // Imágenes y fotografías
    val Image: Shape = RoundedCornerShape(20.dp)

    val Avatar: Shape = RoundedCornerShape(50)

    // Chips y badges
    val Chip: Shape = RoundedCornerShape(50)

    val Badge: Shape = RoundedCornerShape(8.dp)

    // Contenedores
    val Container: Shape = RoundedCornerShape(24.dp)

    // Diálogos
    val Dialog: Shape = RoundedCornerShape(28.dp)

    // Bottom sheets
    val BottomSheet: Shape = RoundedCornerShape(
        topStart = 28.dp,
        topEnd = 28.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )
}
