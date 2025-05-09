package ie.setu.ca1_mad2.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel

// Define light color scheme
private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,
    secondary = YellowSecondary,
    onSecondary = Color.Black,

    background = Color(0xFFF7F7F7),
    surface = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

// Define dark color scheme
private val DarkColorScheme = darkColorScheme(
    primary = BlueDark,
    onPrimary = Color.White,
    secondary = YellowDark,
    onSecondary = Color.Black,

    background = Color(0xFF121212),
    surface = Color(0xFF242424),
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun CA1MAD2Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Create ViewModel with factory
    val context = LocalContext.current
    val themeViewModel: ThemeViewModel = viewModel(
        factory = ThemeViewModel.Factory(context)
    )

    // Get the theme preference from the ViewModel
    val themeMode by themeViewModel.themeMode.collectAsState()

    // Determine if we should use dark theme based on preference or system
    val useDarkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> darkTheme
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    // Choose color scheme based on dark/light theme
    val colorScheme = if (useDarkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}