package mx.edu.utng.smarthealthmonitor.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary          = SHPrimary,
    onPrimary        = SHOnPrimary,
    primaryContainer = SHPrimaryContainer,
    secondary        = SHSecondary,
    error            = SHError,
    background       = SHBackground,
    surface          = SHSurface,
    onSurface        = SHOnSurface,
)

private val DarkColorScheme = darkColorScheme(
    primary    = SHPrimaryDark,
    onPrimary  = SHOnPrimaryDark,
    background = SHBackgroundDark,
    surface    = SHSurfaceDark,
)

@Composable
fun SmartHealthMonitorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}