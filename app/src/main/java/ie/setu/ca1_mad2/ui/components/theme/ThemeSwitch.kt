package ie.setu.ca1_mad2.ui.components.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ie.setu.ca1_mad2.ui.theme.ThemeMode

@Composable
fun ThemeSwitch(
    currentTheme: ThemeMode,
    onThemeChanged: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Light theme option
            ThemeOption(
                title = "Light Mode",
                selected = currentTheme == ThemeMode.LIGHT,
                onClick = { onThemeChanged(ThemeMode.LIGHT) }
            )

            // System theme option
            ThemeOption(
                title = "System Default",
                selected = currentTheme == ThemeMode.SYSTEM,
                onClick = { onThemeChanged(ThemeMode.SYSTEM) }
            )

            // Dark theme option
            ThemeOption(
                title = "Dark Mode",
                selected = currentTheme == ThemeMode.DARK,
                onClick = { onThemeChanged(ThemeMode.DARK) }
            )
        }
    }
}

@Composable
private fun ThemeOption(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null // null because the row is already selectable
        )

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}