package ie.setu.ca1_mad2.ui.components.inputs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ie.setu.ca1_mad2.ui.components.dialogs.MuscleGroupSelectionDialog

@Composable
fun MultiMuscleGroupSelector(
    selectedMuscleGroups: List<String>,
    onSelectionChanged: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    showValidationError: Boolean = false
) {
    val muscleGroups = listOf(
        "Chest", "Back", "Shoulders", "Biceps", "Triceps", "Forearms",
        "Quadriceps", "Hamstrings", "Glutes", "Calves", "Core", "Cardio"
    )

    var showSelectionDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Select Muscle Groups (${selectedMuscleGroups.size} selected)",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // Display selected muscle groups as text
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showSelectionDialog = true }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedMuscleGroups.isEmpty())
                        "Select muscle groups..."
                    else
                        selectedMuscleGroups.joinToString(", "),
                    style = MaterialTheme.typography.bodyMedium
                )

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select muscle groups"
                )
            }
        }

        // Error if requested from showValidationError and list is empty
        if (showValidationError && selectedMuscleGroups.isEmpty()) {
            Text(
                text = "Please select at least one muscle group",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Selection dialog
        if (showSelectionDialog) {
            MuscleGroupSelectionDialog(
                muscleGroups = muscleGroups,
                selectedMuscleGroups = selectedMuscleGroups,
                onSelectionChanged = { newSelection ->
                    onSelectionChanged(newSelection)
                    showSelectionDialog = false
                },
                onDismiss = { showSelectionDialog = false }
            )
        }
    }
}