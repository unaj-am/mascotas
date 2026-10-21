package com.app.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Sistema de formas compartido de Mascotas.
 *
 * Centraliza los radios de las esquinas para
 * mantener consistencia visual entre pantallas.
 */
val MascotasShapes = Shapes(

    // Elementos pequeños: etiquetas e indicadores
    extraSmall = RoundedCornerShape(4.dp),

    // Elementos compactos
    small = RoundedCornerShape(8.dp),

    // Campos de texto y componentes estándar
    medium = RoundedCornerShape(12.dp),

    // Tarjetas y contenedores destacados
    large = RoundedCornerShape(16.dp),

    // Contenedores de mayor tamaño
    extraLarge = RoundedCornerShape(24.dp)
)