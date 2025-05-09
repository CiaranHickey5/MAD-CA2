// Updated file: app/src/main/java/ie/setu/ca1_mad2/ui/screens/SettingsScreen.kt

package ie.setu.ca1_mad2.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ie.setu.ca1_mad2.ui.components.theme.ThemeSwitch
import ie.setu.ca1_mad2.ui.theme.ThemeViewModel

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val themeViewModel: ThemeViewModel = viewModel(
        factory = ThemeViewModel.Factory(context)
    )

    val currentTheme by themeViewModel.themeMode.collectAsState()

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Theme settings
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 16.dp)
            )

            ThemeSwitch(
                currentTheme = currentTheme,
                onThemeChanged = themeViewModel::setThemeMode
            )
        }
    }
}