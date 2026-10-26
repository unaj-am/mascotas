
package com.app.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val OwnerLightColorScheme = lightColorScheme(
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

private val OwnerDarkColorScheme = darkColorScheme(
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

private val WalkerLightColorScheme = lightColorScheme(
    primary = PetWalkerGreen,
    onPrimary = Color.White,
    primaryContainer = PetWalkerGreenLight,
    onPrimaryContainer = PetWalkerGreenDark,

    secondary = PetGreen,
    onSecondary = Color.White,
    tertiary = PetGreenDark,
    onTertiary = Color.White,

    background = PetWalkerBackground,
    onBackground = PetInk,
    surface = Color.White,
    onSurface = PetInk,
    surfaceVariant = PetWalkerMint,
    onSurfaceVariant = PetMuted,

    error = Color(0xFFBA1A1A),
    onError = Color.White
)

private val WalkerDarkColorScheme = darkColorScheme(
    primary = PetWalkerGreenLight,
    onPrimary = PetWalkerGreenDark,
    primaryContainer = PetWalkerGreenDark,
    onPrimaryContainer = PetWalkerGreenLight,

    secondary = PetGreenLight,
    onSecondary = PetGreenDark,
    tertiary = PetWalkerGreenLight,
    onTertiary = PetWalkerGreenDark,

    background = Color(0xFF142019),
    onBackground = Color(0xFFE0EDE2),
    surface = Color(0xFF1D2A22),
    onSurface = Color(0xFFE0EDE2),
    surfaceVariant = Color(0xFF34483A),
    onSurfaceVariant = Color(0xFFC0D2C3),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

/**
 * Tema visual de Mascotas.
 *
 * isWalker = false: paleta del dueño.
 * isWalker = true: paleta del paseador.
 *
 * La tipografía y las formas se comparten entre ambos perfiles.
 */
@Composable
fun MascotasThemeUI(
    isWalker: Boolean = false,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        isWalker && darkTheme -> WalkerDarkColorScheme
        isWalker -> WalkerLightColorScheme
        darkTheme -> OwnerDarkColorScheme
        else -> OwnerLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MascotasTypography,
        shapes = MascotasShapes,
        content = content
    )
}
