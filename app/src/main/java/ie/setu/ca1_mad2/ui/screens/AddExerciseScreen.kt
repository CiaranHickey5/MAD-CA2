package ie.setu.ca1_mad2.ui.screens

import FormField
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ie.setu.ca1_mad2.GymTrackerViewModel
import ie.setu.ca1_mad2.ui.components.inputs.DefaultExerciseSelector
import ie.setu.ca1_mad2.ui.components.inputs.MultiMuscleGroupSelector
import kotlinx.coroutines.delay

@Composable
fun AddExerciseScreen(viewModel: GymTrackerViewModel) {
    var name by remember { mutableStateOf("") }
    var selectedMuscleGroups by remember { mutableStateOf<List<String>>(emptyList()) }
    var exerciseAdded by remember { mutableStateOf(false) }

    var showValidationErrors by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Add New Exercise",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            text = "Select from default exercises:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        DefaultExerciseSelector(
            onExerciseSelected = { exercise ->
                viewModel.addExercise(exercise.name, exercise.muscleGroup)
                // Reset form states
                name = ""
                selectedMuscleGroups = emptyList()
                showValidationErrors = false
                exerciseAdded = true
            },
            modifier = Modifier.fillMaxWidth()
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

        Text(
            text = "Or create custom exercise:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        FormField(
            value = name,
            onValueChange = { name = it },
            label = "Exercise Name",
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        MultiMuscleGroupSelector(
            selectedMuscleGroups = selectedMuscleGroups,
            onSelectionChanged = { selectedMuscleGroups = it },
            showValidationError = showValidationErrors && selectedMuscleGroups.isEmpty()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                showValidationErrors = true

                if (name.isNotBlank() && selectedMuscleGroups.isNotEmpty()) {
                    // Join multiple muscle groups with comma
                    val muscleGroupString = selectedMuscleGroups.joinToString(", ")
                    viewModel.addExercise(name, muscleGroupString)
                    name = ""
                    selectedMuscleGroups = emptyList()
                    showValidationErrors = false
                    exerciseAdded = true
                }
            },
            modifier = Modifier.fillMaxWidth(0.8f),
            enabled = name.isNotBlank()
        ) {
            Text("Add Custom Exercise")
        }

        if (exerciseAdded) {
            SuccessMessage(message = "Exercise has been added!")

            // Reset message after delay
            LaunchedEffect(exerciseAdded) {
                delay(2000)
                exerciseAdded = false
            }
        }
    }
}