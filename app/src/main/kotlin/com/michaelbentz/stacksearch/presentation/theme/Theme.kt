package com.michaelbentz.stacksearch.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = StackOrange,
    onPrimary = Color.White,
    primaryContainer = StackOrangeLight,
    onPrimaryContainer = StackOrangeDark,
    secondary = StackBlue,
    onSecondary = Color.White,
    secondaryContainer = StackBlueLight,
    onSecondaryContainer = StackBlueDark,
    tertiary = TextSecondary,
    onTertiary = Color.White,
    tertiaryContainer = SurfaceVariantLight,
    onTertiaryContainer = TextSecondary,
    background = Color.White,
    onBackground = TextPrimary,
    surface = Color.White,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondary,
    outline = OutlineLight,
    outlineVariant = OutlineLight,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun StackSearchTheme(
    dimens: Dimens = Dimens(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalDimens provides dimens,
    ) {
        MaterialTheme(
            colorScheme = LightColors,
            typography = Typography,
            content = content,
        )
    }
}
