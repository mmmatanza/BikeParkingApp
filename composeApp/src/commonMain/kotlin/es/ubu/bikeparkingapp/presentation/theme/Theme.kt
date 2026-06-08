package es.ubu.bikeparkingapp.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import es.ubu.bikeparkingapp.domain.entity.Theme

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    appliedTheme: Theme? = null,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        appliedTheme != null -> {
            val primary = Color(parseColor(appliedTheme.primaryColor))
            val secondary = Color(parseColor(appliedTheme.secondaryColor))
            if (darkTheme) {
                darkColorScheme(
                    primary = primary,
                    onPrimary = Color.White,
                    primaryContainer = primary,
                    onPrimaryContainer = Color.White,
                    secondary = secondary,
                    onSecondary = Color.White,
                    secondaryContainer = secondary,
                    onSecondaryContainer = Color.White,
                    tertiary = Pink80
                )
            } else {
                lightColorScheme(
                    primary = primary,
                    onPrimary = Color.White,
                    primaryContainer = primary,
                    onPrimaryContainer = Color.White,
                    secondary = secondary,
                    onSecondary = Color.White,
                    secondaryContainer = secondary,
                    onSecondaryContainer = Color.White,
                    tertiary = Pink40
                )
            }
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

private fun parseColor(colorString: String): Long {
    return try {
        val hex = colorString.removePrefix("#")
        val color = if (hex.length == 6) "FF$hex" else hex
        color.toLong(16)
    } catch (e: Exception) {
        0xFF000000 // Negro por defecto si falla la conversión
    }
}
