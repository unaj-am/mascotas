package com.app.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PetGreen,
    onPrimary = Color.White,

    primaryContainer = PetMint,
    onPrimaryContainer = PetGreenDark,

    secondary = PetMuted,
    onSecondary = Color.White,

    tertiary = PetCoral,
    onTertiary = Color.White,

    background = PetCream,
    onBackground = PetInk,

    surface = Color.White,
    onSurface = PetInk,

    surfaceVariant = PetMint,
    onSurfaceVariant = PetMuted,

    error = Color(0xFFBA1A1A),
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = PetGreenLight,
    onPrimary = PetGreenDark,

    primaryContainer = PetGreenDark,
    onPrimaryContainer = PetMint,

    secondary = Color(0xFFB8C9BE),
    onSecondary = PetSurfaceDark,

    tertiary = Color(0xFFFFB4A8),
    onTertiary = PetSurfaceDark,

    background = PetSurfaceDark,
    onBackground = Color(0xFFE0E5DE),

    surface = Color(0xFF202D25),
    onSurface = Color(0xFFE0E5DE),

    surfaceVariant = Color(0xFF34483B),
    onSurfaceVariant = Color(0xFFC0CDC2),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

/**
 * Tema visual compartido de Mascotas.
 *
 * Se define en core:ui para que todas las
 * funcionalidades utilicen el mismo diseño.
 */
@Composable
fun MascotasThemeUI(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MascotasTypography,
        shapes = MascotasShapes,
        content = content
    )
}