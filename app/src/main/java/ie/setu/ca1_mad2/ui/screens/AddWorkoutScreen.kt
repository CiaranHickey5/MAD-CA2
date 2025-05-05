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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ie.setu.ca1_mad2.GymTrackerViewModel
import kotlinx.coroutines.delay

@Composable
fun AddWorkoutScreen(viewModel: GymTrackerViewModel) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var workoutAdded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Add New Workout",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        FormField(
            value = name,
            onValueChange = { name = it },
            label = "Workout Name",
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        FormField(
            value = description,
            onValueChange = { description = it },
            label = "Description",
            singleLine = false,
            modifier = Modifier.height(120.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (name.isNotBlank()) {
                    viewModel.addWorkout(name, description)
                    name = ""
                    description = ""
                    workoutAdded = true
                }
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Add Workout")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (workoutAdded) {
            SuccessMessage(message = "Workout has been added!")

            // Reset message after delay
            LaunchedEffect(workoutAdded) {
                delay(2000)
                workoutAdded = false
            }
        }
    }
}