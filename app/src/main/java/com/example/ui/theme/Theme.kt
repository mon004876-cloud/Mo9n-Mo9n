package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val JoyfulLightColorScheme = lightColorScheme(
    primary = TealPrimary,
    onPrimary = Color.White,
    primaryContainer = TealLightContainer,
    onPrimaryContainer = OnTealLightContainer,
    secondary = CoralSecondary,
    onSecondary = Color.White,
    secondaryContainer = CoralLightContainer,
    onSecondaryContainer = OnCoralLightContainer,
    tertiary = GoldTertiary,
    onTertiary = Color.White,
    tertiaryContainer = GoldLightContainer,
    onTertiaryContainer = OnGoldLightContainer,
    background = CreamBackground,
    onBackground = InkTextPrimary,
    surface = Color.White,
    onSurface = InkTextPrimary,
    surfaceVariant = PaperPageSurface,
    onSurfaceVariant = InkTextSecondary,
    outline = OutlineWarm
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    // Always use light, cheerful colors and Right-To-Left (RTL) layout for Iraqi Arabic textbook
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = JoyfulLightColorScheme,
            typography = Typography,
            content = content
        )
    }
}
