package mx.edu.utng.smarthealthmonitor.tv

import androidx.compose.runtime.Composable
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

private val SmartHealthColorScheme = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF1B4F8A),
    secondary = androidx.compose.ui.graphics.Color(0xFFFFC107)
)

@Composable
fun SmartHealthTvTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SmartHealthColorScheme,
        content = content
    )
}
