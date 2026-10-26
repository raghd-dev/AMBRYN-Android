package com.moonglow.candles.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// GOLD PALETTE
val Gold = Color(0xFF85515A)
val GoldDark = Color(0xFF653943)
val GoldLight = Color(0xFFD7ADA5)
val ChampagneGold = Color(0xFFF3E3DD)

// CREAM / BACKGROUND
val Cream = Color(0xFFF7F1EB)
val CreamDark = Color(0xFFEDE2DC)
val WarmWhite = Color(0xFFFFFAF7)
val CardWhite = Color(0xFFFFFAF7)

// DARK TONES
val Charcoal = Color(0xFF2E2427)
val CharcoalSoft = Color(0xFF4B3B40)
val SoftBrown = Color(0xFF78676A)
val Espresso = Color(0xFF402D38)

// ACCENT COLORS
val Terracotta = Color(0xFFB77364)
val Sage = Color(0xFFB9AFA6)
val DustyRose = Color(0xFFCBA7A5)

// UI COLORS
val GrayText = Color(0xFF998B89)
val DividerColor = Color(0xFFE7D9D4)
val SuccessGreen = Color(0xFF7BAE7F)
val WarningAmber = Color(0xFFE0A458)

// SPECIAL EFFECTS
val GlowColor = Color(0xFFF4D8D0)
val ShadowColor = Color(0x14000000)

// APP BACKGROUND
val Background = Color(0xFFF7F1EB)

private val LightScheme = lightColorScheme(
    primary = Gold,
    onPrimary = Color.White,
    secondary = Sage,
    onSecondary = Charcoal,
    tertiary = Terracotta,
    onTertiary = Color.White,
    background = Background,
    onBackground = Charcoal,
    surface = CardWhite,
    onSurface = Charcoal,
    surfaceVariant = CreamDark,
    onSurfaceVariant = SoftBrown,
    outline = DividerColor,
    error = Color(0xFFE53935),
    onError = Color.White
)

@Composable
fun MoonGlowTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightScheme,
        typography = MoonGlowTypography,  // Fixed: now properly references Typography.kt
        shapes = Shapes(
            small = RoundedCornerShape(10),
            medium = RoundedCornerShape(18),
            large = RoundedCornerShape(28)
        ),
        content = content
    )
}