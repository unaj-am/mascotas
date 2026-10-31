
package com.app.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// =====================================================
// TEMA CLARO - DUEÑO
// =====================================================

private val OwnerLightColorScheme = lightColorScheme(

    primary = PetOwnerPurple,
    onPrimary = Color.White,

    primaryContainer = PetOwnerPurpleLight,
    onPrimaryContainer = PetOwnerPurpleDark,

    secondary = Color(0xFF78678C),
    onSecondary = Color.White,

    secondaryContainer = PetOwnerLavender,
    onSecondaryContainer = PetOwnerPurpleDark,

    tertiary = PetCoral,
    onTertiary = Color.White,

    tertiaryContainer = Color(0xFFFFE4DF),
    onTertiaryContainer = Color(0xFF742D27),

    background = PetOwnerBackground,
    onBackground = PetTextPrimary,

    surface = PetSurface,
    onSurface = PetTextPrimary,

    surfaceVariant = PetOwnerLavender,
    onSurfaceVariant = PetTextSecondary,

    surfaceTint = PetOwnerPurple,

    inverseSurface = Color(0xFF303036),
    inverseOnSurface = Color(0xFFF5F0F8),
    inversePrimary = PetOwnerPurpleLight,

    error = PetError,
    onError = Color.White,

    errorContainer = PetErrorContainer,
    onErrorContainer = Color(0xFF7F2020),

    outline = Color(0xFF8D8495),
    outlineVariant = PetCardBorder,

    scrim = Color(0x99000000)
)

// =====================================================
// TEMA OSCURO - DUEÑO
// =====================================================

private val OwnerDarkColorScheme = darkColorScheme(

    primary = Color(0xFFC9AEF2),
    onPrimary = Color(0xFF38245D),

    primaryContainer = PetOwnerPurpleDark,
    onPrimaryContainer = Color(0xFFEBDDFF),

    secondary = Color(0xFFD0C0E1),
    onSecondary = Color(0xFF352C40),

    secondaryContainer = Color(0xFF4A4057),
    onSecondaryContainer = Color(0xFFEDE2F8),

    tertiary = Color(0xFFFFB4A8),
    onTertiary = Color(0xFF5F1711),

    tertiaryContainer = Color(0xFF7C3029),
    onTertiaryContainer = Color(0xFFFFDAD4),

    background = PetDarkBackground,
    onBackground = PetDarkText,

    surface = PetDarkSurface,
    onSurface = PetDarkText,

    surfaceVariant = Color(0xFF39333F),
    onSurfaceVariant = PetDarkMuted,

    surfaceTint = Color(0xFFC9AEF2),

    inverseSurface = Color(0xFFEAE6EF),
    inverseOnSurface = Color(0xFF302B34),
    inversePrimary = PetOwnerPurple,

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),

    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    outline = Color(0xFF9B91A5),
    outlineVariant = PetDarkBorder,

    scrim = Color(0x99000000)
)

// =====================================================
// TEMA CLARO - PASEO
// =====================================================

private val WalkerLightColorScheme = lightColorScheme(

    primary = PetWalkerGreen,
    onPrimary = Color.White,

    primaryContainer = PetWalkerGreenLight,
    onPrimaryContainer = PetWalkerGreenDark,

    secondary = Color(0xFF637C6C),
    onSecondary = Color.White,

    secondaryContainer = PetWalkerMint,
    onSecondaryContainer = PetWalkerGreenDark,

    tertiary = PetCoral,
    onTertiary = Color.White,

    tertiaryContainer = Color(0xFFFFE4DF),
    onTertiaryContainer = Color(0xFF742D27),

    background = PetWalkerBackground,
    onBackground = PetTextPrimary,

    surface = PetSurface,
    onSurface = PetTextPrimary,

    surfaceVariant = PetWalkerMint,
    onSurfaceVariant = PetTextSecondary,

    surfaceTint = PetWalkerGreen,

    inverseSurface = Color(0xFF303630),
    inverseOnSurface = Color(0xFFF1F5F0),
    inversePrimary = PetWalkerGreenLight,

    error = PetError,
    onError = Color.White,

    errorContainer = PetErrorContainer,
    onErrorContainer = Color(0xFF7F2020),

    outline = Color(0xFF829287),
    outlineVariant = Color(0xFFDCE6DD),

    scrim = Color(0x99000000)
)

// =====================================================
// TEMA OSCURO - PASEADOR
// =====================================================

private val WalkerDarkColorScheme = darkColorScheme(

    primary = Color(0xFFA7D9B9),
    onPrimary = Color(0xFF123C29),

    primaryContainer = PetWalkerGreenDark,
    onPrimaryContainer = Color(0xFFD0EFDA),

    secondary = Color(0xFFB9D0BF),
    onSecondary = Color(0xFF293D30),

    secondaryContainer = Color(0xFF354B3B),
    onSecondaryContainer = Color(0xFFDCEFE0),

    tertiary = Color(0xFFFFB4A8),
    onTertiary = Color(0xFF5F1711),

    tertiaryContainer = Color(0xFF7C3029),
    onTertiaryContainer = Color(0xFFFFDAD4),

    background = Color(0xFF141B17),
    onBackground = Color(0xFFE4ECE5),

    surface = Color(0xFF1D2620),
    onSurface = Color(0xFFE4ECE5),

    surfaceVariant = Color(0xFF344238),
    onSurfaceVariant = Color(0xFFC2D0C5),

    surfaceTint = Color(0xFFA7D9B9),

    inverseSurface = Color(0xFFE4ECE5),
    inverseOnSurface = Color(0xFF29332C),
    inversePrimary = PetWalkerGreen,

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),

    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    outline = Color(0xFF899B8E),
    outlineVariant = PetDarkBorder,

    scrim = Color(0x99000000)
)

// =====================================================
// TEMA PRINCIPAL DE MASCO
// =====================================================

/**
 * Tema visual global de Mascotas.
 *
 * isWalker = f -> Identidad del dueño.
 * isWalker = t -> Identidad del paseador.
 *
 * darkTheme = f -> Modo claro.
 * darkTheme = t  -> Modo oscuro.
 *
 * Los componentes y pantallas deben consumir
 * MaterialTheme, sin volver a aplicar este tema.
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
