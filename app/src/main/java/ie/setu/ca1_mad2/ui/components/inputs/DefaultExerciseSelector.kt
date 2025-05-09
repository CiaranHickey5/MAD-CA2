package ie.setu.ca1_mad2.ui.components.inputs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ie.setu.ca1_mad2.data.DefaultExercises
import ie.setu.ca1_mad2.model.Exercise
// Import the dialogs from the dialog components file
import ie.setu.ca1_mad2.ui.components.dialogs.MuscleGroupFilterDialog
import ie.setu.ca1_mad2.ui.components.dialogs.AllExercisesDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultExerciseSelector(
    onExerciseSelected: (Exercise) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var selectedMuscleGroup by remember { mutableStateOf<String?>(null) }

    // Add state variables to control dialog visibility
    var showMuscleGroupDialog by remember { mutableStateOf(false) }
    var showAllExercisesDialog by remember { mutableStateOf(false) }

    val muscleGroups = remember {
        DefaultExercises.exercises
            .flatMap { it.muscleGroup.split(", ") }
            .distinct()
            .sorted()
    }

    // Show the muscle group filter dialog when requested
    if (showMuscleGroupDialog) {
        MuscleGroupFilterDialog(
            muscleGroups = muscleGroups,
            selectedMuscleGroup = selectedMuscleGroup,
            onMuscleGroupSelected = {
                selectedMuscleGroup = it
                showMuscleGroupDialog = false
            },
            onDismiss = { showMuscleGroupDialog = false }
        )
    }

    // Show the all exercises dialog when requested
    if (showAllExercisesDialog) {
        AllExercisesDialog(
            exercises = DefaultExercises.exercises,
            onExerciseSelected = {
                onExerciseSelected(it)
                showAllExercisesDialog = false
            },
            onDismiss = { showAllExercisesDialog = false }
        )
    }

    Column(modifier = modifier) {
        Text(
            text = "Select from default exercises:",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 2.dp)
        )

        // Muscle group filter
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filter by muscle group:",
                style = MaterialTheme.typography.bodySmall
            )

            ExposedDropdownMenuBox(
                expanded = false,
                onExpandedChange = { },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = selectedMuscleGroup ?: "All muscle groups",
                    onValueChange = { },
                    readOnly = true,
                    modifier = Modifier
                        .menuAnchor()
                        .clickable { isExpanded = false }, // Close search dropdown if open
                    trailingIcon = {
                        IconButton(onClick = {
                            // Show the muscle group filter dialog
                            showMuscleGroupDialog = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter muscle groups"
                            )
                        }
                    },
                    singleLine = true
                )
            }
        }

        // Search exercises dropdown
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Search exercises") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                singleLine = true
            )

            // Filter exercises based on search text and selected muscle group
            val filteredExercises = remember(searchText, selectedMuscleGroup) {
                val baseList = if (selectedMuscleGroup != null) {
                    DefaultExercises.exercises.filter {
                        it.muscleGroup.contains(selectedMuscleGroup!!, ignoreCase = true)
                    }
                } else {
                    DefaultExercises.exercises
                }

                if (searchText.isBlank()) {
                    // Organize by muscle group
                    baseList.groupBy {
                        it.muscleGroup.split(", ").first().trim()
                    }.flatMap { it.value }.take(15)
                } else {
                    baseList.filter {
                        it.name.contains(searchText, ignoreCase = true) ||
                                it.muscleGroup.contains(searchText, ignoreCase = true)
                    }.take(15)
                }
            }

            if (filteredExercises.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false }
                ) {
                    if (searchText.isBlank() && selectedMuscleGroup == null) {
                        // Show muscle group headers when not searching
                        val groupedExercises = filteredExercises.groupBy {
                            it.muscleGroup.split(", ").first().trim()
                        }

                        groupedExercises.forEach { (group, exercises) ->
                            Text(
                                text = group,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 16.dp, vertical = 4.dp)
                            )

                            exercises.forEach { exercise ->
                                ExerciseMenuItem(
                                    exercise = exercise,
                                    onClick = {
                                        onExerciseSelected(exercise)
                                        searchText = ""
                                        isExpanded = false
                                    }
                                )
                            }
                        }
                    } else {
                        // List when searching or filtering
                        filteredExercises.forEach { exercise ->
                            ExerciseMenuItem(
                                exercise = exercise,
                                onClick = {
                                    onExerciseSelected(exercise)
                                    searchText = ""
                                    isExpanded = false
                                }
                            )
                        }
                    }

                    // View all exercises dropdown
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "View all exercises...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        onClick = {
                            // Show the all exercises dialog
                            isExpanded = false
                            showAllExercisesDialog = true
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExerciseMenuItem(
    exercise: Exercise,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Column(
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = exercise.muscleGroup,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        onClick = onClick,
        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
    )
}

