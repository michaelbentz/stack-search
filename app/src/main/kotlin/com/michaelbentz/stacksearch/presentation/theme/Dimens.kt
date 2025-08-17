package com.michaelbentz.stacksearch.presentation.theme

import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Stable
data class Dimens(
    val zero: Dp = 0.dp,

    val spacingTiny: Dp = 4.dp,
    val spacingSmall: Dp = 8.dp,
    val spacingMedium: Dp = 16.dp,
    val spacingLarge: Dp = 24.dp,
    val spacingXLarge: Dp = 32.dp,
    val spacingXXLarge: Dp = 36.dp,
    val spacing2XLarge: Dp = 40.dp,
    val spacing3XLarge: Dp = 48.dp,
    val spacing4XLarge: Dp = 56.dp,
    val spacing5XLarge: Dp = 64.dp,

    val iconSmall: Dp = 18.dp,
    val icon: Dp = 24.dp,
    val iconLarge: Dp = 32.dp,

    val imageSmall: Dp = 18.dp,
    val image: Dp = 24.dp,
    val imageLarge: Dp = 32.dp,
    val imageXLarge: Dp = 48.dp,
    val imageXXLarge: Dp = 56.dp,

    val radiusSmall: Dp = 4.dp,
    val radiusMedium: Dp = 8.dp,
    val radiusLarge: Dp = 16.dp,
    val radiusXLarge: Dp = 24.dp,
    val radiusPill: Dp = 50.dp,

    val strokeHairline: Dp = 1.dp,
    val strokeMedium: Dp = 3.dp,
    val strokeThick: Dp = 4.dp,
)

val LocalDimens = staticCompositionLocalOf<Dimens> {
    error("Dimens not provided")
}
