
package ie.setu.ca1_mad2.ui.screens

import EmptyStateMessage
import FormField
import FormSection
import SectionHeader
import SuccessMessage
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ie.setu.ca1_mad2.GymTrackerViewModel
import ie.setu.ca1_mad2.model.Exercise
import ie.setu.ca1_mad2.model.Workout
import ie.setu.ca1_mad2.ui.components.cards.ExerciseCard
import ie.setu.ca1_mad2.ui.components.dialogs.DeleteConfirmationDialog
import ie.setu.ca1_mad2.ui.components.dialogs.EditExerciseDialog
import ie.setu.ca1_mad2.ui.components.inputs.DefaultExerciseSelector
import ie.setu.ca1_mad2.ui.components.inputs.MultiMuscleGroupSelector
import kotlinx.coroutines.delay

@Composable
fun WorkoutDetailScreen(
    viewModel: GymTrackerViewModel,
    workout: Workout
) {
    // Manage scrolling
    val scrollState = rememberScrollState()

    var exerciseName by remember { mutableStateOf("") }
    var selectedMuscleGroups by remember { mutableStateOf<List<String>>(emptyList()) }
    var exerciseAdded by remember { mutableStateOf(false) }

    // Flag to track form validation errors
    var showValidationErrors by remember { mutableStateOf(false) }

    // State to control visibility of add exercise form
    var showAddExerciseForm by remember { mutableStateOf(false) }

    // States for edit dialog
    var showEditDialog by remember { mutableStateOf(false) }
    var exerciseToEdit by remember { mutableStateOf<Exercise?>(null) }
    var editedName by remember { mutableStateOf("") }
    var editedMuscleGroups by remember { mutableStateOf<List<String>>(emptyList()) }

    // States for delete dialog
    var showDeleteDialog by remember { mutableStateOf(false) }
    var exerciseToDelete by remember { mutableStateOf<Exercise?>(null) }

    // Edit Exercise Dialog
    if (showEditDialog && exerciseToEdit != null) {
        EditExerciseDialog(
            exercise = exerciseToEdit!!,
            name = editedName,
            muscleGroup = exerciseToEdit!!.muscleGroup,
            onNameChange = { editedName = it },
            onMuscleGroupChange = { newGroups ->
                editedMuscleGroups = newGroups
            },
            onSave = {
                exerciseToEdit?.let { exercise ->
                    viewModel.updateWorkoutExercise(
                        workoutId = workout.id.toString(),
                        exerciseId = exercise.id.toString(),
                        newName = editedName,
                        newMuscleGroup = editedMuscleGroups.joinToString(", ")
                    )
                }
                showEditDialog = false
                exerciseToEdit = null
            },
            onDismiss = {
                showEditDialog = false
                exerciseToEdit = null
            }
        )
    }

    // Delete Exercise Dialog
    if (showDeleteDialog && exerciseToDelete != null) {
        DeleteConfirmationDialog(
            title = "Delete Exercise",
            itemName = exerciseToDelete?.name ?: "",
            onConfirm = {
                exerciseToDelete?.let { exercise ->
                    viewModel.removeExerciseFromWorkout(
                        workoutId = workout.id.toString(),
                        exerciseId = exercise.id.toString()
                    )
                }
                showDeleteDialog = false
                exerciseToDelete = null
            },
            onDismiss = {
                showDeleteDialog = false
                exerciseToDelete = null
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // Workout header
        Text(
            text = workout.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        if (workout.description.isNotBlank()) {
            Text(
                text = workout.description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Exercises Section
        SectionHeader(title = "Exercises")

        if (workout.exercises.isEmpty()) {
            EmptyStateMessage(message = "No exercises added to this workout yet")
        } else {
            // Display each exercise as a card
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                workout.exercises.forEach { exercise ->
                    ExerciseCard(
                        exercise = exercise,
                        onEditClicked = {
                            exerciseToEdit = exercise
                            editedName = exercise.name
                            editedMuscleGroups = exercise.muscleGroup.split(", ").filter { it.isNotBlank() }
                            showEditDialog = true
                        },
                        onDeleteClicked = {
                            exerciseToDelete = exercise
                            showDeleteDialog = true
                        }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add Exercise Button
        Button(
            onClick = { showAddExerciseForm = !showAddExerciseForm },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (showAddExerciseForm) "Hide Add Exercise Form" else "Add Exercise to Workout")
        }

        // Add Exercise Form
        if (showAddExerciseForm) {
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            FormSection(title = "Add Exercise to Workout") {
                Text(
                    text = "Select from default exercises:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                DefaultExerciseSelector(
                    onExerciseSelected = { exercise ->
                        viewModel.addExerciseToWorkout(workout.id.toString(), exercise.name, exercise.muscleGroup)
                        // Reset custom exercise
                        exerciseName = ""
                        selectedMuscleGroups = emptyList()
                        showValidationErrors = false
                        exerciseAdded = true
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                Text(
                    text = "Or create custom exercise:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                FormField(
                    value = exerciseName,
                    onValueChange = { exerciseName = it },
                    label = "Exercise Name"
                )

                Spacer(modifier = Modifier.height(12.dp))

                MultiMuscleGroupSelector(
                    selectedMuscleGroups = selectedMuscleGroups,
                    onSelectionChanged = { selectedMuscleGroups = it },
                    showValidationError = showValidationErrors && selectedMuscleGroups.isEmpty()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        showValidationErrors = true
                        if (exerciseName.isNotBlank() && selectedMuscleGroups.isNotEmpty()) {
                            // Join groups with comma
                            val muscleGroupString = selectedMuscleGroups.joinToString(", ")
                            viewModel.addExerciseToWorkout(workout.id.toString(), exerciseName, muscleGroupString)
                            exerciseName = ""
                            selectedMuscleGroups = emptyList()
                            showValidationErrors = false
                            exerciseAdded = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = exerciseName.isNotBlank()
                ) {
                    Text("Add Custom Exercise")
                }

                if (exerciseAdded) {
                    SuccessMessage(message = "Exercise added successfully!")

                    // Reset message after delay
                    LaunchedEffect(exerciseAdded) {
                        delay(2000)
                        exerciseAdded = false
                    }
                }
            }
        }
    }
}