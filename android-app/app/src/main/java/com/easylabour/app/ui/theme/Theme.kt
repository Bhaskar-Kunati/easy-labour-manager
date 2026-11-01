package com.easylabour.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF66B2FF),
    secondary = Color(0xFFFF9933),
    tertiary = Color(0xFF80CBC4),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E)
)

private val LightColorScheme = lightColorScheme(

    primary = DeepPlum,
    onPrimary = WhiteText,
    secondary = Gold,
    onSecondary = WhiteText,
    tertiary = ChampagneGold,
    background = OffWhite,
    surface = PureWhite,
    onSurface = AlmostBlack,
    onSurfaceVariant = DarkGray,
    surfaceVariant = LightGray
)

@Composable
fun EasyLabourTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
