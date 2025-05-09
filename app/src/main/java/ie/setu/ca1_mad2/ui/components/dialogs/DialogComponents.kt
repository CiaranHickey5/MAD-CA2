package ie.setu.ca1_mad2.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ie.setu.ca1_mad2.model.Exercise
import ie.setu.ca1_mad2.model.Workout
import ie.setu.ca1_mad2.ui.components.inputs.MultiMuscleGroupSelector

@Composable
fun DeleteConfirmationDialog(
    title: String,
    itemName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text("Are you sure you want to delete '$itemName'? This action cannot be undone.") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditWorkoutDialog(
    workout: Workout,
    name: String,
    description: String,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Workout") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Workout Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                enabled = name.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditExerciseDialog(
    exercise: Exercise,
    name: String,
    muscleGroup: String,
    onNameChange: (String) -> Unit,
    onMuscleGroupChange: (List<String>) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    // Parse current muscle groups from string
    val currentMuscleGroups = remember(muscleGroup) {
        muscleGroup.split(", ").filter { it.isNotBlank() }
    }

    var selectedMuscleGroups by remember { mutableStateOf(currentMuscleGroups) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Exercise") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Exercise Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                MultiMuscleGroupSelector(
                    selectedMuscleGroups = selectedMuscleGroups,
                    onSelectionChanged = {
                        selectedMuscleGroups = it
                        onMuscleGroupChange(it)
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                enabled = name.isNotBlank() && selectedMuscleGroups.isNotEmpty()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun MuscleGroupFilterDialog(
    muscleGroups: List<String>,
    selectedMuscleGroup: String?,
    onMuscleGroupSelected: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter by Muscle Group") },
        text = {
            LazyColumn {
                item {
                    ListItem(
                        headlineContent = { Text("All muscle groups") },
                        leadingContent = {
                            RadioButton(
                                selected = selectedMuscleGroup == null,
                                onClick = { onMuscleGroupSelected(null) }
                            )
                        },
                        modifier = Modifier.clickable { onMuscleGroupSelected(null) }
                    )
                }

                items(muscleGroups) { muscleGroup ->
                    ListItem(
                        headlineContent = { Text(muscleGroup) },
                        leadingContent = {
                            RadioButton(
                                selected = selectedMuscleGroup == muscleGroup,
                                onClick = { onMuscleGroupSelected(muscleGroup) }
                            )
                        },
                        modifier = Modifier.clickable { onMuscleGroupSelected(muscleGroup) }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun AllExercisesDialog(
    exercises: List<Exercise>,
    onExerciseSelected: (Exercise) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("All Exercises") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                // Group exercises by primary muscle group
                val groupedExercises = exercises.groupBy {
                    it.muscleGroup.split(", ").first().trim()
                }.toSortedMap()

                LazyColumn {
                    groupedExercises.forEach { (muscleGroup, exercisesForMuscle) ->
                        item {
                            Text(
                                text = muscleGroup,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }

                        items(exercisesForMuscle) { exercise ->
                            ListItem(
                                headlineContent = { Text(exercise.name) },
                                supportingContent = { Text(exercise.muscleGroup) },
                                modifier = Modifier
                                    .clickable {
                                        onExerciseSelected(exercise)
                                    }
                                    .padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}