package ie.setu.ca1_mad2.ui.screens

import EmptyStateMessage
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ie.setu.ca1_mad2.GymTrackerViewModel
import ie.setu.ca1_mad2.model.Workout
import ie.setu.ca1_mad2.ui.components.cards.WorkoutCard
import ie.setu.ca1_mad2.ui.components.dialogs.DeleteConfirmationDialog
import ie.setu.ca1_mad2.ui.components.dialogs.EditWorkoutDialog
import ie.setu.ca1_mad2.ui.components.filters.WorkoutFilterSheet
import androidx.compose.material.icons.filled.FilterList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListWorkoutsScreen(
    workouts: List<Workout>,
    viewModel: GymTrackerViewModel,
    onWorkoutSelected: (Workout) -> Unit
) {
    // State for deletion confirmation
    var workoutToDelete by remember { mutableStateOf<Workout?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // State for editing workout
    var workoutToEdit by remember { mutableStateOf<Workout?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf("") }
    var editedDescription by remember { mutableStateOf("") }

    // Filter state
    val filterMuscleGroups by viewModel.filterMuscleGroups.collectAsState()
    val filteredWorkouts by viewModel.filteredWorkouts.collectAsState()
    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // Filter bottom sheet
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState
        ) {
            WorkoutFilterSheet(
                selectedMuscleGroups = filterMuscleGroups,
                onSelectionChanged = viewModel::updateFilterMuscleGroups,
                onClearFilters = viewModel::clearFilters,
                onDismiss = { showFilterSheet = false }
            )
        }
    }

    // Edit Workout Dialog
    if (showEditDialog && workoutToEdit != null) {
        EditWorkoutDialog(
            workout = workoutToEdit!!,
            name = editedName,
            description = editedDescription,
            onNameChange = { editedName = it },
            onDescriptionChange = { editedDescription = it },
            onSave = {
                workoutToEdit?.let { workout ->
                    viewModel.updateWorkout(
                        workoutId = workout.id,
                        newName = editedName,
                        newDescription = editedDescription
                    )
                }
                showEditDialog = false
                workoutToEdit = null
            },
            onDismiss = {
                showEditDialog = false
                workoutToEdit = null
            }
        )
    }

    // Delete confirmation dialog
    if (showDeleteDialog && workoutToDelete != null) {
        DeleteConfirmationDialog(
            title = "Delete Workout",
            itemName = workoutToDelete?.name ?: "",
            onConfirm = {
                workoutToDelete?.let { workout ->
                    viewModel.deleteWorkout(workout.id)
                }
                showDeleteDialog = false
                workoutToDelete = null
            },
            onDismiss = {
                showDeleteDialog = false
                workoutToDelete = null
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header row with filter button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your Workouts",
                style = MaterialTheme.typography.headlineMedium
            )

            // Filter button with indicator badge if filters are active
            IconButton(onClick = { showFilterSheet = true }) {
                Box {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filter workouts"
                    )

                    if (filterMuscleGroups.isNotEmpty()) {
                        Badge(
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Text(filterMuscleGroups.size.toString())
                        }
                    }
                }
            }
        }

        // Display a chip showing active filters
        if (filterMuscleGroups.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Filtered by: ${filterMuscleGroups.joinToString(", ")}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        IconButton(
                            onClick = viewModel::clearFilters,
                            modifier = Modifier.padding(start = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear filters",
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Show filtered workouts
        if (filteredWorkouts.isEmpty()) {
            if (filterMuscleGroups.isNotEmpty()) {
                EmptyStateMessage(message = "No workouts match your filter criteria")
            } else {
                EmptyStateMessage(message = "No workouts yet. Add your first workout!")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredWorkouts) { workout ->
                    WorkoutCard(
                        workout = workout,
                        onWorkoutSelected = onWorkoutSelected,
                        onDeleteClicked = {
                            workoutToDelete = workout
                            showDeleteDialog = true
                        },
                        onEditClicked = {
                            workoutToEdit = workout
                            editedName = workout.name
                            editedDescription = workout.description
                            showEditDialog = true
                        }
                    )
                }
            }
        }
    }
}