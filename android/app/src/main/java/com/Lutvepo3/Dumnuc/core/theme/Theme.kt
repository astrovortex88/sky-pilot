package com.Lutvepo3.Dumnuc.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val SkyPilotColors = darkColorScheme(
    primary = Primary,
    onPrimary = TextPrimary,
    primaryContainer = PrimaryBright,
    onPrimaryContainer = TextPrimary,
    secondary = Secondary,
    onSecondary = BgDeep,
    secondaryContainer = SurfaceAlt,
    onSecondaryContainer = TextPrimary,
    tertiary = Accent,
    onTertiary = BgDeep,
    tertiaryContainer = AccentSoft,
    onTertiaryContainer = BgDeep,
    background = BgDeep,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceAlt,
    onSurfaceVariant = TextSecondary,
    outline = TextMuted,
    outlineVariant = SurfaceAlt,
    error = Danger,
    onError = TextPrimary,
    scrim = BgSplashTop
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SkyPilotColors,
        typography = AppTypography,
        content = content
    )
}
