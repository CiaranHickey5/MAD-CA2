package ie.setu.ca1_mad2.ui.screens

import EmptyStateMessage
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ie.setu.ca1_mad2.model.Exercise

@Composable
fun ListExerciseScreen(
    exercises: List<Exercise>
) {
    // Outer Column for heading and list
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Your Exercises",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )
        HorizontalDivider()

        if (exercises.isEmpty()) {
            EmptyStateMessage(message = "No exercises yet. Add your first exercise!")
        } else {
            // Inside for actual list
            LazyColumn(
                modifier = Modifier.padding(16.dp)
            ) {
                items(exercises) { exercise ->
                    Text(
                        "• ${exercise.name} - ${exercise.muscleGroup}",
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}