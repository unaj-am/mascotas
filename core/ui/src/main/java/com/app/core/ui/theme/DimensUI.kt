package com.app.core.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Sistema de dimensiones compartido de Mascotas.
 *
 * Centraliza los espaciados y tamaños visuales
 * utilizados por los componentes de la aplicación.
 */
object DimensUI {

    // Escala de espaciados
    val SpaceNone: Dp = 0.dp
    val SpaceXXSmall: Dp = 2.dp
    val SpaceXSmall: Dp = 4.dp
    val SpaceSmall: Dp = 8.dp
    val SpaceMedium: Dp = 12.dp
    val SpaceLarge: Dp = 16.dp
    val SpaceXLarge: Dp = 24.dp
    val SpaceXXLarge: Dp = 32.dp

    // Márgenes de pantalla
    val ScreenHorizontalPadding: Dp = 16.dp
    val ScreenVerticalPadding: Dp = 24.dp

    // Espaciado entre secciones
    val SectionSpacing: Dp = 24.dp

    // Dimensiones de componentes
    val ButtonMinHeight: Dp = 48.dp
    val TextFieldMinHeight: Dp = 56.dp

    // Elementos visuales
    val IconSizeSmall: Dp = 16.dp
    val IconSizeMedium: Dp = 24.dp
    val IconSizeLarge: Dp = 32.dp

    // Imágenes de mascotas
    val PetAvatarSize: Dp = 64.dp
    val PetImageSize: Dp = 120.dp

    // Altura de las acciones rápidas del dashboard
    val QuickActionHeight: Dp = 100.dp
}