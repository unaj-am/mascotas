package com.app.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PetOwnerPurple,
    onPrimary = Color.White,

    primaryContainer = PetOwnerPurpleLight,
    onPrimaryContainer = PetOwnerPurpleDark,

    secondary = PetMuted,
    onSecondary = Color.White,

    tertiary = PetOwnerPurple,
    onTertiary = Color.White,

    background = PetOwnerBackground,
    onBackground = PetInk,

    surface = Color.White,
    onSurface = PetInk,

    surfaceVariant = PetOwnerLavender,
    onSurfaceVariant = PetMuted,

    error = Color(0xFFBA1A1A),
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = PetOwnerPurpleLight,
    onPrimary = PetOwnerPurpleDark,

    primaryContainer = PetOwnerPurpleDark,
    onPrimaryContainer = PetOwnerPurpleLight,

    secondary = Color(0xFFCEC0E0),
    onSecondary = Color(0xFF302044),

    tertiary = PetOwnerPurpleLight,
    onTertiary = PetOwnerPurpleDark,

    background = Color(0xFF19151F),
    onBackground = Color(0xFFEAE1F0),

    surface = Color(0xFF24202B),
    onSurface = Color(0xFFEAE1F0),

    surfaceVariant = Color(0xFF40364D),
    onSurfaceVariant = Color(0xFFD0C3DC),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

/**
 * Tema visual compartido de Mascotas.
 *
 * Todas las funcionalidades utilizan la misma
 * identidad visual, independientemente del perfil.
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